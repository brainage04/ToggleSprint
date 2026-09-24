package com.github.brainage04.togglesprint.waypoint

import com.github.brainage04.togglesprint.commands.SimpleCommand
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.Locale

/**
 * `/waypoints`: manage the current world's waypoints. Names containing spaces are quoted, e.g.
 * `/waypoints add "My Base"`.
 */
object WaypointsCommand {
    private val subcommands = listOf("add", "remove", "list")
    private var openScreenNextTick = false

    fun register() {
        ClientCommandHandler.instance.registerCommand(SimpleCommand(
            "waypoints",
            object : SimpleCommand.ProcessCommandRunnable() {
                override fun processCommand(sender: ICommandSender?, args: Array<String>?) {
                    execute(args.orEmpty().toList())
                }
            },
            object : SimpleCommand.TabCompleteRunnable {
                override fun tabComplete(sender: ICommandSender?, args: Array<String>?, pos: BlockPos?): List<String> {
                    return tabComplete(args.orEmpty().toList())
                }
            },
        ))
    }

    /** The chat screen closes once the command returns, so the waypoints screen opens on the next tick. */
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END || !openScreenNextTick) return
        openScreenNextTick = false
        Minecraft.getMinecraft().displayGuiScreen(WaypointsScreen(null))
    }

    private fun execute(args: List<String>) {
        if (args.isEmpty()) {
            openScreenNextTick = true
            return
        }

        val words = splitQuoted(args.drop(1).joinToString(" "))
        when (args[0].lowercase(Locale.ROOT)) {
            "add" -> add(words)
            "remove" -> {
                if (words?.size == 1) WaypointActions.remove(words[0]) else usage("/waypoints remove <name>")
            }
            "list" -> if (args.size == 1) list() else usage("/waypoints list")
            else -> usage("/waypoints [add|remove|list]")
        }
    }

    private fun add(words: List<String>?) {
        when (words?.size) {
            1 -> WaypointActions.createAtPlayer(words[0])
            4 -> {
                val coordinates = words.drop(1).map { it.toIntOrNull() }
                if (coordinates.any { it == null }) {
                    WaypointActions.message("X, Y and Z must be whole numbers.", ChatUtils.PrefixType.RED)
                } else {
                    WaypointActions.create(words[0], BlockPos(coordinates[0]!!, coordinates[1]!!, coordinates[2]!!))
                }
            }
            else -> usage("/waypoints add <name> [<x> <y> <z>]")
        }
    }

    private fun list() {
        val waypoints = WaypointStore.current().orEmpty()
        if (waypoints.isEmpty()) {
            WaypointActions.message("No waypoints in this world. Add one with /waypoints add <name>.")
            return
        }

        WaypointActions.message("Waypoints:")
        for (waypoint in waypoints) {
            val hidden = if (waypoint.visible) "" else " [hidden]"
            WaypointActions.message(" - ${WaypointActions.name(waypoint)} ${waypoint.coordinates()} (${WaypointActions.dimensionName(waypoint.dimension)})$hidden")
        }
    }

    private fun usage(usage: String) {
        WaypointActions.message("Usage: $usage", ChatUtils.PrefixType.RED)
    }

    private fun tabComplete(args: List<String>): List<String> {
        if (args.size <= 1) {
            val prefix = args.firstOrNull().orEmpty().lowercase(Locale.ROOT)
            return subcommands.filter { it.startsWith(prefix) }
        }
        if (!args[0].equals("remove", ignoreCase = true)) return emptyList()

        // the name may span several words, but only the last word is replaced by a completion
        val typed = args.drop(1).joinToString(" ")
        val lastWord = args.last()
        return WaypointStore.current().orEmpty()
            .map { quoteIfNeeded(it.name) }
            .filter { it.startsWith(typed, ignoreCase = true) }
            .map { lastWord + it.substring(typed.length) }
    }

    /** Wraps [name] in quotes when it contains a space, so that it is read back as one word. */
    fun quoteIfNeeded(name: String): String = if (name.contains(' ')) "\"$name\"" else name

    /**
     * Splits [text] on spaces, keeping "quoted text" together as one word without its quotes.
     * Returns null when a quote is left open.
     */
    fun splitQuoted(text: String): List<String>? {
        val words = ArrayList<String>()
        val word = StringBuilder()
        var quoted = false
        var inWord = false
        for (character in text) {
            when {
                character == '"' -> {
                    quoted = !quoted
                    inWord = true
                }
                character == ' ' && !quoted -> {
                    if (inWord) words.add(word.toString())
                    word.setLength(0)
                    inWord = false
                }
                else -> {
                    word.append(character)
                    inWord = true
                }
            }
        }
        if (quoted) return null
        if (inWord) words.add(word.toString())
        return words
    }
}
