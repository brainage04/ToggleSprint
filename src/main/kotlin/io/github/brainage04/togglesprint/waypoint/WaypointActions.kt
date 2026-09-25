package io.github.brainage04.togglesprint.waypoint

import io.github.brainage04.togglesprint.util.ChatFeedback
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos

/** Waypoint changes shared by the command, the keys and the screens; each reports in chat. */
object WaypointActions {
    /** Minecraft's 16 chat colours, indexed by their § code (0-f). */
    private val chatColours = intArrayOf(
        0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF,
    )

    /** Creates a waypoint at the player's feet, named "Waypoint N" when [name] is null. */
    fun createAtPlayer(name: String?): Waypoint? {
        val player = Minecraft.getMinecraft().thePlayer ?: return null
        return create(name, BlockPos(player))
    }

    /** Creates a waypoint in the player's dimension, named "Waypoint N" when [name] is null. */
    fun create(name: String?, pos: BlockPos): Waypoint? {
        val player = Minecraft.getMinecraft().thePlayer
        val waypoints = WaypointStore.current()
        if (waypoints == null || player == null) {
            ChatFeedback.error("Waypoints can only be created in a world.")
            return null
        }

        val waypointName = name?.trim() ?: WaypointStore.nextQuickName(waypoints)
        validateName(waypointName)?.let {
            ChatFeedback.error(it)
            return null
        }
        if (WaypointStore.find(waypoints, waypointName) != null) {
            ChatFeedback.error("A waypoint called \"$waypointName\" already exists.")
            return null
        }

        val waypoint = Waypoint(waypointName, pos.x, pos.y, pos.z, player.dimension, WaypointStore.nextColour(waypoints))
        waypoints.add(waypoint)
        WaypointStore.save()

        val text = "Created waypoint ${name(waypoint)} at ${waypoint.coordinates()}."
        if (ConfigUtils.brainageHudParity.waypoints.showInWorld) {
            ChatFeedback.success(text)
        } else {
            ChatFeedback.warning("$text Turn on Show In World in the Waypoints config to see it.")
        }
        return waypoint
    }

    fun remove(name: String): Boolean {
        val waypoints = WaypointStore.current()
        val waypoint = waypoints?.let { WaypointStore.find(it, name) }
        if (waypoints == null || waypoint == null) {
            ChatFeedback.error("There is no waypoint called \"$name\".")
            return false
        }

        waypoints.remove(waypoint)
        WaypointStore.save()
        ChatFeedback.success("Removed waypoint ${name(waypoint)}.")
        return true
    }

    /** Why [name] cannot be a waypoint name, or null if it can. */
    fun validateName(name: String): String? = when {
        name.isEmpty() -> "A waypoint needs a name."
        name.length > 64 -> "Waypoint names can be at most 64 characters long."
        name.any { it == '§' || it == '"' || Character.isISOControl(it) } -> "Waypoint names cannot contain § or \"."
        else -> null
    }

    /** The waypoint's name for chat, in the chat colour nearest to its colour. */
    fun name(waypoint: Waypoint): String = "§${chatColourCode(waypoint.colour)}${waypoint.name}§r"

    /** The § code (0-9, a-f) of the chat colour nearest to [rgb]. */
    fun chatColourCode(rgb: Int): Char {
        var best = 0
        var bestDistance = Int.MAX_VALUE
        for ((index, colour) in chatColours.withIndex()) {
            val red = (colour shr 16 and 0xFF) - (rgb shr 16 and 0xFF)
            val green = (colour shr 8 and 0xFF) - (rgb shr 8 and 0xFF)
            val blue = (colour and 0xFF) - (rgb and 0xFF)
            val distance = red * red + green * green + blue * blue
            if (distance < bestDistance) {
                best = index
                bestDistance = distance
            }
        }
        return Character.forDigit(best, 16)
    }

    fun dimensionName(dimension: Int): String = when (dimension) {
        0 -> "Overworld"
        -1 -> "Nether"
        1 -> "The End"
        else -> "Dimension $dimension"
    }
}
