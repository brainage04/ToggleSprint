package com.github.brainage04.togglesprint.waypoint

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale
import kotlin.math.floor

/**
 * The user's waypoints, kept per world in `config/togglesprint/waypoints.json`. A world is a
 * singleplayer save (by folder name) or a server (by address). Used only on the client thread.
 */
object WaypointStore {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val colours = intArrayOf(
        0xFF5555, 0x55FF55, 0x5555FF, 0xFFFF55, 0xFF55FF, 0x55FFFF,
        0xFFAA00, 0xAA00AA, 0x00AAAA, 0xFFFFFF, 0xAAAAAA, 0x00AA00,
    )

    private var worlds: MutableMap<String, MutableList<Waypoint>>? = null

    /** The current world's waypoints, or null outside a world. The list is live. */
    fun current(): MutableList<Waypoint>? {
        val key = currentWorldKey() ?: return null
        val list = load().getOrPut(key) { ArrayList() }
        importLegacyWaypoints(list)
        return list
    }

    /** Imports the old waypoint list as soon as a world is joined, whether or not waypoints are shown. */
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getMinecraft().thePlayer == null) return
        if (ConfigUtils.brainageHudParity.waypoints.entries.isNullOrEmpty()) return
        current()
    }

    fun currentWorldKey(): String? {
        val minecraft = Minecraft.getMinecraft()
        if (minecraft.theWorld == null) return null

        val server = minecraft.integratedServer
        if (minecraft.isIntegratedServerRunning && server != null) return "singleplayer/${server.folderName}"

        val serverData = minecraft.currentServerData ?: return null
        return "server/${serverData.serverIP.lowercase(Locale.ROOT)}"
    }

    /** Writes every world's waypoints to disk. */
    fun save() {
        val file = file()
        try {
            Files.createDirectories(file.parentFile.toPath())
            val temporary = File(file.parentFile, "${file.name}.tmp")
            Files.newBufferedWriter(temporary.toPath(), StandardCharsets.UTF_8).use { gson.toJson(load(), it) }
            Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (exception: IOException) {
            ToggleSprintMain.LOGGER.error("Could not save waypoints to $file", exception)
        }
    }

    /** The waypoint called [name], ignoring case. */
    fun find(waypoints: List<Waypoint>, name: String): Waypoint? =
        waypoints.firstOrNull { it.name.equals(name, ignoreCase = true) }

    /** The first of "Waypoint 1", "Waypoint 2", ... that no waypoint in the list is called. */
    fun nextQuickName(waypoints: List<Waypoint>): String {
        var number = 1
        while (find(waypoints, "Waypoint $number") != null) number++
        return "Waypoint $number"
    }

    /** Cycles through a palette so that consecutive waypoints are told apart. */
    fun nextColour(waypoints: List<Waypoint>): Int = colours[waypoints.size % colours.size]

    /**
     * Moves the waypoints of the old config list (which had no world) into [list], the first world
     * joined after updating, then clears the old list. Needs a player to report the import to.
     */
    private fun importLegacyWaypoints(list: MutableList<Waypoint>) {
        if (Minecraft.getMinecraft().thePlayer == null) return
        val legacy = ConfigUtils.brainageHudParity.waypoints.entries ?: return
        if (legacy.isEmpty()) return

        var imported = 0
        for (old in legacy) {
            val name = old.name?.trim().orEmpty()
            if (name.isEmpty() || find(list, name) != null) continue
            if (!old.x.isFinite() || !old.y.isFinite() || !old.z.isFinite()) continue
            list.add(Waypoint(name, floor(old.x).toInt(), floor(old.y).toInt(), floor(old.z).toInt(), old.dimension, nextColour(list)))
            imported++
        }
        legacy.clear()
        save()
        ToggleSprintMain.configManager.save()
        if (imported > 0) {
            ChatUtils.messageToChat("Moved $imported waypoint${if (imported == 1) "" else "s"} from the old waypoint list into this world. See /waypoints.")
        }
    }

    private fun load(): MutableMap<String, MutableList<Waypoint>> {
        worlds?.let { return it }

        val loadedWorlds = LinkedHashMap<String, MutableList<Waypoint>>()
        worlds = loadedWorlds
        val file = file()
        if (!file.exists()) return loadedWorlds

        try {
            Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8).use { reader ->
                val type = object : TypeToken<LinkedHashMap<String, ArrayList<Waypoint>>>() {}.type
                val loaded: LinkedHashMap<String, ArrayList<Waypoint>>? = gson.fromJson(reader, type)
                loaded?.forEach { (key, waypoints) ->
                    // Gson bypasses Kotlin's null checks: drop null lists and entries without a name
                    @Suppress("SENSELESS_COMPARISON")
                    if (waypoints != null) loadedWorlds[key] = waypoints.filterTo(ArrayList()) { it != null && it.name != null }
                }
            }
        } catch (exception: Exception) {
            if (exception !is IOException && exception !is JsonParseException) throw exception
            // keep the unreadable file rather than overwriting it with an empty list on the next save
            val backup = File(file.parentFile, "${file.name}.unreadable")
            ToggleSprintMain.LOGGER.error("Could not read waypoints from $file; moving it to $backup", exception)
            try {
                Files.move(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (moveException: IOException) {
                ToggleSprintMain.LOGGER.error("Could not move $file aside", moveException)
            }
        }

        return loadedWorlds
    }

    private fun file(): File = File(Minecraft.getMinecraft().mcDataDir, "config/${ToggleSprintMain.MOD_ID}/waypoints.json")
}
