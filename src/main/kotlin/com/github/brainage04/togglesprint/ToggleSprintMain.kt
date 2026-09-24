package com.github.brainage04.togglesprint

import com.github.brainage04.togglesprint.commands.CommandManager
import com.github.brainage04.togglesprint.commands.EnchantCommands
import com.github.brainage04.togglesprint.waypoint.WaypointRenderer
import com.github.brainage04.togglesprint.waypoint.WaypointsCommand
import com.github.brainage04.togglesprint.waypoint.WaypointStore
import com.github.brainage04.togglesprint.config.manager.ConfigManager
import com.github.brainage04.togglesprint.config.ToggleSprintConfig
import com.github.brainage04.togglesprint.events.ClientTickEventTracker
import com.github.brainage04.togglesprint.events.InputEventTracker
import com.github.brainage04.togglesprint.events.FullbrightHandler
import com.github.brainage04.togglesprint.events.NetworkPacketMonitor
import com.github.brainage04.togglesprint.events.EnchantTooltipHandler
import com.github.brainage04.togglesprint.gui.PerformanceHud
import com.github.brainage04.togglesprint.gui.TPSTracker
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.keybinds.ConfigKeybind
import com.github.brainage04.togglesprint.keybinds.ElementEditorKeybind
import com.github.brainage04.togglesprint.keybinds.CreateWaypointKeybind
import com.github.brainage04.togglesprint.keybinds.ManageWaypointsKeybind
import com.github.brainage04.togglesprint.keybinds.StatsKeybind
import com.github.brainage04.togglesprint.keybinds.ToggleSneakKeybind
import com.github.brainage04.togglesprint.keybinds.ToggleSprintKeybind
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
    modid = ToggleSprintMain.MOD_ID,
    clientSideOnly = true,
    useMetadata = true,
    guiFactory = "com.github.brainage04.togglesprint.config.ModGuiFactory",
)
class ToggleSprintMain {
    private fun registerKeyBinds(vararg keybinds: KeyBinding?) {
        for (keybind in keybinds) {
            ClientRegistry.registerKeyBinding(keybind)
        }
    }

    private fun registerEvents(vararg events: Any?) {
        for (event in events) {
            MinecraftForge.EVENT_BUS.register(event)
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        CommandManager()
        WaypointsCommand.register()
        EnchantCommands.registerCommands()

        registerKeyBinds(
            toggleSprintKeybind,
            toggleSneakKeybind,
            statsKeybind,
            configKeybind,
            elementEditorKeybind,
            createWaypointKeybind,
            manageWaypointsKeybind,
        )
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        configManager = ConfigManager()
        MinecraftForge.EVENT_BUS.register(configManager)

        registerEvents(
            statsKeybind,
            configKeybind,
            elementEditorKeybind,
            createWaypointKeybind,
            manageWaypointsKeybind,

            NetworkPacketMonitor,
            InputEventTracker(),
            ClientTickEventTracker(),
            RenderGuiData(),
            TPSTracker,
            FullbrightHandler(),
            EnchantTooltipHandler(),
            PerformanceHud,
            WaypointRenderer,
            WaypointsCommand,
            WaypointStore,
        )
    }

    companion object {
        lateinit var configManager: ConfigManager
        const val MOD_ID = "togglesprint"
        const val MOD_NAME = "Toggle Sprint"

        val LOGGER: Logger = LogManager.getLogger(MOD_ID)

        @JvmStatic
        val version: String
            get() = Loader.instance().indexedModList[MOD_ID]!!.version

        val config: ToggleSprintConfig
            get() = configManager.config ?: error("config is null")

        val toggleSprintKeybind: KeyBinding = ToggleSprintKeybind()
        val toggleSneakKeybind: KeyBinding = ToggleSneakKeybind()
        val statsKeybind: KeyBinding = StatsKeybind()
        val configKeybind: KeyBinding = ConfigKeybind()
        val elementEditorKeybind: KeyBinding = ElementEditorKeybind()
        val createWaypointKeybind: KeyBinding = CreateWaypointKeybind()
        val manageWaypointsKeybind: KeyBinding = ManageWaypointsKeybind()
    }
}
