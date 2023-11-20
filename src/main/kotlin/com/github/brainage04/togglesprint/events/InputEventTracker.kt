package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.isSneakToggled
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.isSprintToggled
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import org.lwjgl.input.Keyboard

class InputEventTracker {
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    @SubscribeEvent
    fun onInputEvent(event: InputEvent) {
        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        // sprint handling
        if (toggleMovementKeys.toggleSprint.isEnabled && toggleSprintKeybind.isPressed) {
            isSprintToggled = !isSprintToggled

            if (!isSprintToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, Keyboard.isKeyDown(minecraft.gameSettings.keyBindSprint.keyCode))
            }
        }

        if (isSprintToggled && !minecraft.gameSettings.keyBindSprint.isPressed && minecraft.currentScreen == null) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, true)

        // sneak handling
        if (toggleMovementKeys.toggleSneak.isEnabled && toggleSneakKeybind.isPressed) {
            isSneakToggled = !isSneakToggled

            if (!isSneakToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, Keyboard.isKeyDown(minecraft.gameSettings.keyBindSneak.keyCode))
            }
        }

        if (isSneakToggled && !minecraft.gameSettings.keyBindSneak.isPressed && minecraft.currentScreen == null) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, true)
    }
}