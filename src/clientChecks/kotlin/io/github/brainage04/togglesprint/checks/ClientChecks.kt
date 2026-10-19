package io.github.brainage04.togglesprint.checks

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.config.ConfigEditorScreen
import io.github.brainage04.togglesprint.config.ModGuiFactory
import io.github.brainage04.togglesprint.config.ToggleSprintConfig
import io.github.brainage04.togglesprint.config.manager.ConfigManager
import io.github.brainage04.togglesprint.hud.core.HudElementEditor
import io.github.brainage04.togglesprint.util.EnchantmentUtils
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.waypoint.WaypointStore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.gui.GuiScreen
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
    private lateinit var originalLight: FloatArray
    private lateinit var firstWorldLight: FloatArray

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
                    enchantCommandChecks()
                    for (name in ClientRegressions.inWorld()) pass(name)
                    firstWorldLight = minecraft.theWorld.provider.lightBrightnessTable
                    originalLight = firstWorldLight.clone()
                    command("/fullbright 1")
                    // Wait for the actual overlay subscriber to draw this element.
                    ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings.isEnabled = true
                    advance()
                }
                2 -> if (stageTicks > 10) {
                    val bounds = RenderGuiData.boundsOf(ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings)
                    check(bounds != null && bounds.width > 0 && bounds.height > 0) { "HUD was not drawn by the overlay subscriber" }
                    pass("Forge overlay draws an enabled HUD element")
                    check(firstWorldLight.all { it == 1.0f }) { "Fullbright did not change the live light table" }
                    pass("Fullbright changes the active dimension light table")
                    command("/fullbright 0")
                    ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings.isEnabled = false
                    advance()
                }
                3 -> if (stageTicks > 10) {
                    check(firstWorldLight.contentEquals(originalLight)) { "Fullbright did not restore the original light table" }
                    pass("Disabling fullbright restores the exact original light table")
                    check(RenderGuiData.boundsOf(ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings) == null)
                    pass("Disabling a HUD element stops its rendering")
                    ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings.isEnabled = true
                    command("/togglesprint")
                    advance()
                }
                4 -> if (stageTicks > 2) {
                    check(minecraft.currentScreen is ConfigEditorScreen) { "Config command did not open the editor" }
                    escapeEditor()
                    check(minecraft.currentScreen == null) { "In-world editor did not return to the game" }
                    pass("Config command opens and closes in-world")
                    editorUndoCheck()
                    command("/waypoints add \"First World\" 1 64 2")
                    command("/fullbright 1")
                    advance()
                }
                5 -> if (stageTicks > 10) {
                    check(firstWorldLight.all { it == 1.0f })
                    ClientRegressions.prepareReachCache()
                    minecraft.netHandler.networkManager.disableAutoRead()
                    advance()
                }
                6 -> if (stageTicks > 2) {
                    // Drain scheduled packets while the integrated server is still running.
                    minecraft.theWorld.sendQuittingDisconnectingPacket()
                    minecraft.loadWorld(null)
                    check(firstWorldLight.contentEquals(originalLight)) { "World unload left the light override applied" }
                    pass("World unload restores the old dimension light table")
                    ToggleSprintMain.config.brainageHudParity.fullbright = 0f
                    minecraft.displayGuiScreen(GuiMainMenu())
                    advance()
                }
                7 -> {
                    minecraft.launchIntegratedServer("client-checks-second", "Client checks second",
                        WorldSettings(43L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT))
                    advance()
                }
                8 -> if (minecraft.thePlayer != null && stageTicks > 40) {
                    val secondWorldWaypoints = checkNotNull(WaypointStore.current())
                    check(WaypointStore.find(secondWorldWaypoints, "First World") == null)
                    pass("Joining another world does not expose the first world's waypoints")
                    ClientRegressions.reachAfterWorldChange()
                    pass("Reach click mode drops its cached target when changing worlds")
                    minecraft.netHandler.networkManager.disableAutoRead()
                    advance()
                }
                9 -> if (stageTicks > 2) {
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
        ToggleSprintMain.config.toggleMovementKeys.toggleSprint.isEnabled = false
        press(ToggleSprintMain.toggleSprintKeybind)
        check(!ToggleSprintTracker.isSprintToggled)
        ToggleSprintMain.config.toggleMovementKeys.toggleSprint.isEnabled = true
        MinecraftForge.EVENT_BUS.post(InputEvent.KeyInputEvent())
        check(!ToggleSprintTracker.isSprintToggled && !minecraft.gameSettings.keyBindSprint.isKeyDown)
        pass("Disabled toggle presses do not replay after re-enabling the key")
    }

    private fun commandChecks() {
        command("/fullbright 0.75")
        check(ToggleSprintMain.config.brainageHudParity.fullbright == 0.75f)
        val file = File(Minecraft.getMinecraft().mcDataDir, "config/togglesprint/config.json")
        val saved = ConfigManager.gson.fromJson(file.readText(), ToggleSprintConfig::class.java)
        check(saved.brainageHudParity.fullbright == 0.75f) { "Command config did not persist to disk" }
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

    private fun enchantCommandChecks() {
        commandWithChat("/getenchants minecraft:diamond_pickaxe", "Acceptable enchants", "Fortune", "Silk Touch")
        commandWithChat("/getenchantinfo fortune", "ID: minecraft:fortune", "Max level: 3")
        commandWithChat("/getenchantinfo PROTECTION", "ID: minecraft:protection")
        commandWithChat("/getenchantinfo protection_that_does_not_exist", "No potential matches")
        pass("Enchant commands resolve real registry IDs and names and report missing names")
        command("/blacklistedenchants add minecraft:fortune")
        check("minecraft:fortune" in EnchantmentUtils.blacklist())
        commandWithChat("/blacklistedenchants query", "Fortune")
        command("/blacklistedenchants remove minecraft:fortune")
        check("minecraft:fortune" !in EnchantmentUtils.blacklist())
        pass("Blacklist commands update and query real enchantment config")
    }

    private fun commandWithChat(text: String, vararg expected: String) {
        val chat = Minecraft.getMinecraft().ingameGUI.chatGUI
        chat.clearChatMessages()
        command(text)
        val field = GuiNewChat::class.java.getDeclaredField("chatLines")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val lines = field.get(chat) as List<ChatLine>
        val output = lines.joinToString("\n") { it.chatComponent.unformattedText }
        for (fragment in expected) check(output.contains(fragment)) { "$text did not print $fragment: $output" }
    }

    private fun editorUndoCheck() {
        val minecraft = Minecraft.getMinecraft()
        val settings = ToggleSprintMain.config.guiElements.toggleSprintElement.coreSettings
        val originalX = settings.x
        minecraft.displayGuiScreen(HudElementEditor())
        settings.x = originalX + 40
        key(minecraft.currentScreen, Keyboard.KEY_ESCAPE)
        check(settings.x == originalX && minecraft.currentScreen == null)
        val file = File(minecraft.mcDataDir, "config/togglesprint/config.json")
        val saved = ConfigManager.gson.fromJson(file.readText(), ToggleSprintConfig::class.java)
        check(saved.guiElements.toggleSprintElement.coreSettings.x == originalX)
        pass("Element-editor Escape reverts positions before saving")
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
        key(Minecraft.getMinecraft().currentScreen, Keyboard.KEY_ESCAPE)
    }

    private fun key(screen: GuiScreen, keyCode: Int) {
        val method = screen.javaClass.getDeclaredMethod("keyTyped", Char::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        method.isAccessible = true
        method.invoke(screen, '\u001b', keyCode)
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
        val result = if (failure == null) "PASS (${passed.size} scenarios)\n${passed.joinToString("\n")}" else "FAIL at stage $stage\n${failure.stackTraceToString()}"
        stage = -1
        File(Minecraft.getMinecraft().mcDataDir, "result.txt").writeText(result)
        failure?.printStackTrace()
        Minecraft.getMinecraft().theWorld?.sendQuittingDisconnectingPacket()
        Minecraft.getMinecraft().loadWorld(null)
        Minecraft.getMinecraft().shutdown()
    }
}
