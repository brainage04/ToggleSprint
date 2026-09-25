package io.github.brainage04.togglesprint.event

import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import io.github.brainage04.togglesprint.event.ClientTickEventTracker.Companion.updateToggleSprintSneakStates
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSneakToggled
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSprintToggled
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

class InputEventTracker {
    @SubscribeEvent
    fun onInputEvent(event: InputEvent) {
        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        // sprint handling
        if (ConfigUtils.toggleMovementKeys.toggleSprint.isEnabled && toggleSprintKeybind.isPressed) {
            isSprintToggled = !isSprintToggled

            if (!isSprintToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, Keyboard.isKeyDown(minecraft.gameSettings.keyBindSprint.keyCode))
            }
        }

        // sneak handling
        if (ConfigUtils.toggleMovementKeys.toggleSneak.isEnabled && toggleSneakKeybind.isPressed) {
            isSneakToggled = !isSneakToggled

            if (!isSneakToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, Keyboard.isKeyDown(minecraft.gameSettings.keyBindSneak.keyCode))
            }
        }

        updateToggleSprintSneakStates(minecraft)
    }
}