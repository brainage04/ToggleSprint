package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.commands.SimpleCommand
import com.github.brainage04.togglesprint.config.categories.BrainageHudParity
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.client.ClientCommandHandler
import kotlin.math.atan2
import kotlin.math.round

/** Renders locally stored waypoints; it never sends waypoint data to a server. */
object WaypointHud {
    private val arrows = arrayOf("↑", "↗", "→", "↘", "↓", "↙", "←", "↖")

    fun render() {
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return
        if (minecraft.theWorld == null) return

        val config = ConfigUtils.brainageHudParity.waypoints
        if (!config.coreSettings.isEnabled) return

        val maximumEntries = config.maximumEntries.coerceIn(1, 20)
        val visible = orderedWaypoints(config.entries, player.dimension, player.posX, player.posY, player.posZ)
            .take(maximumEntries)
        if (visible.isEmpty()) return

        val text = ArrayList<String>(visible.size)
        for (waypoint in visible) {
            val distance = roundedDistance(waypoint, player.posX, player.posY, player.posZ)
            text.add("${ConfigUtils.primaryChars}${waypoint.name}: $distance blocks ${relativeArrow(waypoint.x - player.posX, waypoint.z - player.posZ, player.rotationYaw)}")
        }
        RenderGuiData.renderElement(config.coreSettings.x, config.coreSettings.y, config.coreSettings.anchorCorner, text)
    }

    fun orderedWaypoints(entries: List<BrainageHudParity.Waypoint>, dimension: Int, x: Double, y: Double, z: Double): List<BrainageHudParity.Waypoint> {
        return entries.asSequence()
            .filter { it.dimension == dimension && validCoordinates(it.x, it.y, it.z) }
            .sortedWith(compareBy<BrainageHudParity.Waypoint> { distanceSquared(it, x, y, z) }
                .thenBy { it.name ?: "" }
                .thenBy { it.x }
                .thenBy { it.y }
                .thenBy { it.z })
            .toList()
    }

    fun roundedDistance(waypoint: BrainageHudParity.Waypoint, x: Double, y: Double, z: Double): Long {
        return round(kotlin.math.sqrt(distanceSquared(waypoint, x, y, z))).toLong()
    }

    /** Returns an eight-way arrow relative to the player's current horizontal facing. */
    fun relativeArrow(deltaX: Double, deltaZ: Double, playerYaw: Float): String {
        if (deltaX == 0.0 && deltaZ == 0.0) return arrows[0]
        val bearing = Math.toDegrees(atan2(deltaX, deltaZ))
        val index = Math.floorMod(kotlin.math.floor((-bearing - playerYaw + 22.5) / 45.0).toInt(), arrows.size)
        return arrows[index]
    }

    private fun distanceSquared(waypoint: BrainageHudParity.Waypoint, x: Double, y: Double, z: Double): Double {
        val dx = waypoint.x - x
        val dy = waypoint.y - y
        val dz = waypoint.z - z
        return dx * dx + dy * dy + dz * dz
    }

    private fun validCoordinates(x: Double, y: Double, z: Double): Boolean {
        return !x.isNaN() && !x.isInfinite() && !y.isNaN() && !y.isInfinite() && !z.isNaN() && !z.isInfinite()
    }
}

/** Client-only `/tswaypoint` command registration and persistence mutations. */
object WaypointCommands {
    fun registerCommands() {
        ClientCommandHandler.instance.registerCommand(SimpleCommand("tswaypoint", object : SimpleCommand.ProcessCommandRunnable() {
            override fun processCommand(sender: net.minecraft.command.ICommandSender?, args: Array<String>?) {
                execute(args ?: emptyArray())
            }
        }))
    }

    fun execute(args: Array<String>) {
        if (args.isEmpty()) {
            usage()
            return
        }
        when (args[0].lowercase(java.util.Locale.ROOT)) {
            "add" -> add(args)
            "remove" -> remove(args)
            "list" -> list(args)
            "clear" -> clear(args)
            else -> usage()
        }
    }

    private fun add(args: Array<String>) {
        if (args.size != 2 && args.size != 5 && args.size != 6) {
            message("Usage: /tswaypoint add <name> [x y z [dimension]]", ChatUtils.PrefixType.RED)
            return
        }
        val name = args[1].trim()
        if (name.isEmpty() || name.length > 32 || name.indexOf('§') >= 0 || name.any { Character.isISOControl(it) }) {
            message("Waypoint names must be 1-32 visible characters.", ChatUtils.PrefixType.RED)
            return
        }

        val player = Minecraft.getMinecraft().thePlayer
        val values = if (args.size == 2) {
            if (player == null) {
                message("Join a world or provide coordinates.", ChatUtils.PrefixType.RED)
                return
            }
            doubleArrayOf(player.posX, player.posY, player.posZ)
        } else {
            val parsed = doubleArrayOf(parseCoordinate(args[2]), parseCoordinate(args[3]), parseCoordinate(args[4]))
            if (parsed.any { it.isNaN() }) {
                message("Coordinates must be finite numbers.", ChatUtils.PrefixType.RED)
                return
            }
            parsed
        }
        val dimension = when (args.size) {
            6 -> args[5].toIntOrNull()
            else -> player?.dimension
        }
        if (dimension == null) {
            message("Provide a dimension when no player is available.", ChatUtils.PrefixType.RED)
            return
        }

        val entries = ConfigUtils.brainageHudParity.waypoints.entries
        val replaced = entries.removeAll { it.name != null && it.name.equals(name, ignoreCase = true) }
        entries.add(BrainageHudParity.Waypoint(name, values[0], values[1], values[2], dimension))
        ToggleSprintMain.configManager.save()
        message(if (replaced) "Replaced waypoint '$name'." else "Added waypoint '$name'.", ChatUtils.PrefixType.GREEN)
    }

    private fun remove(args: Array<String>) {
        if (args.size != 2) {
            message("Usage: /tswaypoint remove <name>", ChatUtils.PrefixType.RED)
            return
        }
        val removed = ConfigUtils.brainageHudParity.waypoints.entries.removeAll { it.name != null && it.name.equals(args[1], ignoreCase = true) }
        if (!removed) {
            message("No waypoint named '${args[1]}'.", ChatUtils.PrefixType.RED)
            return
        }
        ToggleSprintMain.configManager.save()
        message("Removed waypoint '${args[1]}'.", ChatUtils.PrefixType.GREEN)
    }

    private fun list(args: Array<String>) {
        if (args.size != 1) {
            message("Usage: /tswaypoint list", ChatUtils.PrefixType.RED)
            return
        }
        val entries = ConfigUtils.brainageHudParity.waypoints.entries.sortedWith(compareBy<BrainageHudParity.Waypoint> { it.name ?: "" }.thenBy { it.dimension })
        if (entries.isEmpty()) {
            message("No local waypoints saved.")
            return
        }
        message("Local waypoints (${entries.size}):")
        for (waypoint in entries) message("${waypoint.name} (${waypoint.x}, ${waypoint.y}, ${waypoint.z}; dim ${waypoint.dimension})")
    }

    private fun clear(args: Array<String>) {
        if (args.size != 1) {
            message("Usage: /tswaypoint clear", ChatUtils.PrefixType.RED)
            return
        }
        val entries = ConfigUtils.brainageHudParity.waypoints.entries
        if (entries.isEmpty()) {
            message("No local waypoints to clear.")
            return
        }
        val count = entries.size
        entries.clear()
        ToggleSprintMain.configManager.save()
        message("Cleared $count local waypoint${if (count == 1) "" else "s"}.", ChatUtils.PrefixType.GREEN)
    }

    private fun parseCoordinate(value: String): Double {
        val parsed = value.toDoubleOrNull() ?: return Double.NaN
        return if (parsed.isInfinite() || parsed.isNaN()) Double.NaN else parsed
    }

    private fun usage() {
        message("Usage: /tswaypoint <add|remove|list|clear>", ChatUtils.PrefixType.RED)
    }

    private fun message(text: String, type: ChatUtils.PrefixType = ChatUtils.PrefixType.DEFAULT) {
        if (Minecraft.getMinecraft().thePlayer != null) ChatUtils.messageToChat(text, type)
    }
}
