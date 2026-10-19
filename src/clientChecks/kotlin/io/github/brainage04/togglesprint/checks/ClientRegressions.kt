package io.github.brainage04.togglesprint.checks

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.config.ToggleSprintConfig
import io.github.brainage04.togglesprint.config.manager.ConfigManager
import io.github.brainage04.togglesprint.hud.ReachHud
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiNewChat
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File
import java.nio.file.Files
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Regression assertions use the real client's bindings, inventory, files and world events. */
object ClientRegressions {
    fun inWorld(): List<String> {
        mouseMovementBinding()
        inventoryStatsIncludesArmour()
        configReadersClose()
        concurrentSaves()
        return listOf(
            "Mouse-bound sprint and sneak can be toggled off without a keyboard-index error",
            "Inventory Stats includes armour even when the main inventory is empty",
            "Loading config closes the actual config file descriptor",
            "Concurrent config saves finish with valid persisted settings",
        )
    }

    private fun mouseMovementBinding() {
        val minecraft = Minecraft.getMinecraft()
        val sprint = minecraft.gameSettings.keyBindSprint
        val sneak = minecraft.gameSettings.keyBindSneak
        val sprintCode = sprint.keyCode
        val sneakCode = sneak.keyCode
        try {
            sprint.keyCode = -97
            sneak.keyCode = -96
            KeyBinding.resetKeyBindingArrayAndHash()
            ToggleSprintTracker.isSprintToggled = true
            ToggleSprintTracker.isSneakToggled = true
            KeyBinding.setKeyBindState(sprint.keyCode, true)
            KeyBinding.setKeyBindState(sneak.keyCode, true)
            KeyBinding.onTick(ToggleSprintMain.toggleSprintKeybind.keyCode)
            KeyBinding.onTick(ToggleSprintMain.toggleSneakKeybind.keyCode)
            MinecraftForge.EVENT_BUS.post(InputEvent.KeyInputEvent())
            check(!ToggleSprintTracker.isSprintToggled && !sprint.isKeyDown)
            check(!ToggleSprintTracker.isSneakToggled && !sneak.isKeyDown)
        } finally {
            sprint.keyCode = sprintCode
            sneak.keyCode = sneakCode
            KeyBinding.resetKeyBindingArrayAndHash()
        }
    }

    private fun inventoryStatsIncludesArmour() {
        val minecraft = Minecraft.getMinecraft()
        val inventory = minecraft.thePlayer.inventory
        val oldMain = inventory.mainInventory.clone()
        val oldArmour = inventory.armorInventory.clone()
        val binding = ToggleSprintMain.inventoryStatsKeybind
        val oldCode = binding.keyCode
        try {
            inventory.mainInventory.fill(null)
            inventory.armorInventory.fill(null)
            inventory.armorInventory[0] = ItemStack(Items.diamond_boots)
            binding.keyCode = Keyboard.KEY_F12
            KeyBinding.resetKeyBindingArrayAndHash()
            minecraft.ingameGUI.chatGUI.clearChatMessages()
            KeyBinding.onTick(binding.keyCode)
            MinecraftForge.EVENT_BUS.post(TickEvent.ClientTickEvent(TickEvent.Phase.START))
            val field = GuiNewChat::class.java.getDeclaredField("chatLines")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val lines = field.get(minecraft.ingameGUI.chatGUI) as List<ChatLine>
            val output = lines.joinToString("\n") { it.chatComponent.unformattedText }
            check(output.contains("Slot 36: 1 × minecraft:diamond_boots")) { output }
            check(!output.contains("Your inventory is empty")) { output }
        } finally {
            System.arraycopy(oldMain, 0, inventory.mainInventory, 0, oldMain.size)
            System.arraycopy(oldArmour, 0, inventory.armorInventory, 0, oldArmour.size)
            binding.keyCode = oldCode
            KeyBinding.resetKeyBindingArrayAndHash()
        }
    }

    private fun configReadersClose() {
        ToggleSprintMain.configManager.save()
        val file = File(Minecraft.getMinecraft().mcDataDir, "config/togglesprint/config.json").canonicalFile
        val manager = ToggleSprintMain.configManager
        val originalConfig = manager.config
        try {
            val load = ConfigManager::class.java.getDeclaredMethod("tryReadConfig")
            load.isAccessible = true
            load.invoke(manager)
        } finally {
            manager.config = originalConfig
        }
        val descriptors = File("/proc/self/fd").listFiles() ?: error("Cannot inspect config descriptors")
        val openConfig = descriptors.filter {
            try {
                Files.readSymbolicLink(it.toPath()).toString() == file.path
            } catch (_: java.io.IOException) {
                false // An unrelated network descriptor may close while the directory is inspected.
            }
        }
        check(openConfig.isEmpty()) { "Loading config leaked descriptors: $openConfig" }
    }

    private fun concurrentSaves() {
        val start = CountDownLatch(1)
        val failures = Collections.synchronizedList(ArrayList<Throwable>())
        val workers = (1..4).map {
            Thread {
                try {
                    check(start.await(5, TimeUnit.SECONDS))
                    repeat(8) { ToggleSprintMain.configManager.save() }
                } catch (failure: Throwable) {
                    failures.add(failure)
                }
            }.apply { name = "togglesprint-save-check-$it"; start() }
        }
        try {
            start.countDown()
            workers.forEach { it.join(10_000) }
            check(workers.none { it.isAlive }) { "Config-save workers did not stop" }
            check(failures.isEmpty()) { "Concurrent saves failed: $failures" }
            val file = File(Minecraft.getMinecraft().mcDataDir, "config/togglesprint/config.json")
            val saved = ConfigManager.gson.fromJson(file.readText(), ToggleSprintConfig::class.java)
            check(saved.brainageHudParity.fullbright == ToggleSprintMain.config.brainageHudParity.fullbright)
        } finally {
            workers.filter { it.isAlive }.forEach { it.interrupt() }
            workers.forEach { it.join(1000) }
        }
    }

    fun prepareReachCache() {
        val minecraft = Minecraft.getMinecraft()
        val config = ToggleSprintMain.config.brainageHudParity.reach
        config.coreSettings.isEnabled = false
        config.showName = true
        config.showCoordinates = true
        config.updateOnAttackClick = false
        minecraft.objectMouseOver = MovingObjectPosition(Vec3(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos(0, 0, 0))
        check(ReachHud.lines().isNotEmpty())
        config.updateOnAttackClick = true
        check(ReachHud.lines().isNotEmpty())
        KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindAttack.keyCode, false)
        minecraft.objectMouseOver = null
    }

    fun reachAfterWorldChange() {
        Minecraft.getMinecraft().objectMouseOver = null
        check(ReachHud.lines().isEmpty()) { "Reach click mode reused the old world's target" }
    }
}
