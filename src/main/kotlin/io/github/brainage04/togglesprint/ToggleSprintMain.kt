package io.github.brainage04.togglesprint

import io.github.brainage04.togglesprint.command.CommandManager
import io.github.brainage04.togglesprint.command.EnchantCommands
import io.github.brainage04.togglesprint.waypoint.WaypointRenderer
import io.github.brainage04.togglesprint.waypoint.WaypointsCommand
import io.github.brainage04.togglesprint.waypoint.WaypointStore
import io.github.brainage04.togglesprint.config.manager.ConfigManager
import io.github.brainage04.togglesprint.config.ToggleSprintConfig
import io.github.brainage04.togglesprint.event.ClientTickEventTracker
import io.github.brainage04.togglesprint.event.InputEventTracker
import io.github.brainage04.togglesprint.event.FullbrightHandler
import io.github.brainage04.togglesprint.event.NetworkPacketMonitor
import io.github.brainage04.togglesprint.event.EnchantTooltipHandler
import io.github.brainage04.togglesprint.hud.PerformanceHud
import io.github.brainage04.togglesprint.hud.NetworkTracker
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.keys.ConfigKeybind
import io.github.brainage04.togglesprint.keys.ElementEditorKeybind
import io.github.brainage04.togglesprint.keys.CreateWaypointKeybind
import io.github.brainage04.togglesprint.keys.ManageWaypointsKeybind
import io.github.brainage04.togglesprint.keys.InventoryStatsKeybind
import io.github.brainage04.togglesprint.keys.ToggleSneakKeybind
import io.github.brainage04.togglesprint.keys.ToggleSprintKeybind
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
    guiFactory = "io.github.brainage04.togglesprint.config.ModGuiFactory",
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
            inventoryStatsKeybind,
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
            inventoryStatsKeybind,
            configKeybind,
            elementEditorKeybind,
            createWaypointKeybind,
            manageWaypointsKeybind,

            NetworkPacketMonitor,
            InputEventTracker(),
            ClientTickEventTracker(),
            RenderGuiData(),
            NetworkTracker,
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
        val inventoryStatsKeybind: KeyBinding = InventoryStatsKeybind()
        val configKeybind: KeyBinding = ConfigKeybind()
        val elementEditorKeybind: KeyBinding = ElementEditorKeybind()
        val createWaypointKeybind: KeyBinding = CreateWaypointKeybind()
        val manageWaypointsKeybind: KeyBinding = ManageWaypointsKeybind()
    }
}
