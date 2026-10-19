package io.github.brainage04.togglesprint.checks

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.config.ConfigEditorScreen
import io.github.brainage04.togglesprint.config.ModGuiFactory
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.waypoint.WaypointStore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.settings.KeyBinding
import net.minecraft.world.WorldSettings
import net.minecraft.world.WorldType
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.GuiModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File

/** A test-only Forge mod, never included in the shipped jar. */
@Mod(modid = "togglesprint_client_checks", name = "Toggle Sprint client checks", version = "test",
    clientSideOnly = true, dependencies = "required-after:togglesprint")
class ClientChecks {
    private var stage = 0
    private var ticks = 0
    private var stageTicks = 0
    private val passed = ArrayList<String>()

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END || stage == -1) return
        val minecraft = Minecraft.getMinecraft()
        try {
            check(++ticks < 2400) { "Timed out at stage $stage" }
            stageTicks++
            when (stage) {
                0 -> if (minecraft.currentScreen is GuiMainMenu) {
                    val parent = GuiModList(minecraft.currentScreen)
                    minecraft.displayGuiScreen(ModGuiFactory.ConfigGuiScreen(parent))
                    check(minecraft.currentScreen is ConfigEditorScreen)
                    escapeEditor()
                    check(minecraft.currentScreen === parent) { "Config editor did not return to Mods" }
                    pass("Mods-list config returns to its parent")
                    advance()
                    minecraft.launchIntegratedServer("client-checks", "Client checks",
                        WorldSettings(42L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT))
                }
                1 -> if (minecraft.thePlayer != null && stageTicks > 40) {
                    minecraft.displayGuiScreen(null)
                    inputChecks()
                    commandChecks()
                    // Wait for the actual overlay subscriber to draw this element.
                    ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings.isEnabled = true
                    advance()
                }
                2 -> if (stageTicks > 10) {
                    val bounds = RenderGuiData.boundsOf(ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings)
                    check(bounds != null && bounds.width > 0 && bounds.height > 0) { "HUD was not drawn by the overlay subscriber" }
                    pass("Forge overlay draws an enabled HUD element")
                    command("/togglesprint")
                    advance()
                }
                3 -> if (stageTicks > 2) {
                    check(minecraft.currentScreen is ConfigEditorScreen) { "Config command did not open the editor" }
                    escapeEditor()
                    check(minecraft.currentScreen == null) { "In-world editor did not return to the game" }
                    pass("Config command opens and closes in-world")
                    finish(null)
                }
            }
        } catch (failure: Throwable) {
            finish(failure)
        }
    }

    private fun inputChecks() {
        val minecraft = Minecraft.getMinecraft()
        ToggleSprintTracker.isSprintToggled = false
        ToggleSprintTracker.isSneakToggled = false
        ToggleSprintMain.config.toggleMovementKeys.toggleSprint.isEnabled = true
        ToggleSprintMain.config.toggleMovementKeys.toggleSneak.isEnabled = true
        press(ToggleSprintMain.toggleSprintKeybind)
        check(ToggleSprintTracker.isSprintToggled && minecraft.gameSettings.keyBindSprint.isKeyDown)
        press(ToggleSprintMain.toggleSprintKeybind)
        check(!ToggleSprintTracker.isSprintToggled && !minecraft.gameSettings.keyBindSprint.isKeyDown)
        press(ToggleSprintMain.toggleSneakKeybind)
        check(ToggleSprintTracker.isSneakToggled && minecraft.gameSettings.keyBindSneak.isKeyDown)
        press(ToggleSprintMain.toggleSneakKeybind)
        check(!ToggleSprintTracker.isSneakToggled && !minecraft.gameSettings.keyBindSneak.isKeyDown)
        pass("Forge input toggles and releases sprint and sneak")
    }

    private fun commandChecks() {
        command("/fullbright 0.75")
        check(ToggleSprintMain.config.brainageHudParity.fullbright == 0.75f)
        command("/fullbright NaN")
        check(ToggleSprintMain.config.brainageHudParity.fullbright == 0.75f)
        command("/fullbright 0")
        pass("Fullbright command changes config and rejects NaN")
        command("/waypoints add \"Client Base\" 12 64 -7")
        val waypoint = WaypointStore.find(WaypointStore.current().orEmpty(), "Client Base")
        check(waypoint != null && waypoint.x == 12 && waypoint.y == 64 && waypoint.z == -7)
        command("/waypoints remove \"Client Base\"")
        check(WaypointStore.find(WaypointStore.current().orEmpty(), "Client Base") == null)
        pass("Waypoint commands add and remove a quoted name")
    }

    private fun press(binding: KeyBinding) {
        KeyBinding.onTick(binding.keyCode)
        MinecraftForge.EVENT_BUS.post(InputEvent.KeyInputEvent())
    }

    private fun command(text: String) {
        val minecraft = Minecraft.getMinecraft()
        check(ClientCommandHandler.instance.executeCommand(minecraft.thePlayer, text) == 1) { "Command not handled: $text" }
    }

    private fun escapeEditor() {
        val screen = Minecraft.getMinecraft().currentScreen
        val method = ConfigEditorScreen::class.java.getDeclaredMethod("keyTyped", Char::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        method.isAccessible = true
        method.invoke(screen, '\u001b', Keyboard.KEY_ESCAPE)
    }

    private fun pass(name: String) {
        passed.add(name)
        println("CLIENT CHECK PASS: $name")
    }

    private fun advance() {
        stage++
        stageTicks = 0
    }

    private fun finish(failure: Throwable?) {
        stage = -1
        val result = if (failure == null) "PASS (${passed.size} scenarios)\n${passed.joinToString("\n")}" else "FAIL at stage $stage\n${failure.stackTraceToString()}"
        File(Minecraft.getMinecraft().mcDataDir, "result.txt").writeText(result)
        failure?.printStackTrace()
        Minecraft.getMinecraft().loadWorld(null)
        Minecraft.getMinecraft().shutdown()
    }
}
