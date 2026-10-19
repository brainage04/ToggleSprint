package io.github.brainage04.togglesprint.event

import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import io.github.brainage04.togglesprint.event.ClientTickEventTracker.Companion.updateToggleSprintSneakStates
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSneakToggled
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSprintToggled
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

class InputEventTracker {
    @SubscribeEvent
    fun onInputEvent(event: InputEvent) {
        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        // sprint handling
        if (toggleSprintKeybind.isPressed && ConfigUtils.toggleMovementKeys.toggleSprint.isEnabled) {
            isSprintToggled = !isSprintToggled

            if (!isSprintToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, GameSettings.isKeyDown(minecraft.gameSettings.keyBindSprint))
            }
        }

        // sneak handling
        if (toggleSneakKeybind.isPressed && ConfigUtils.toggleMovementKeys.toggleSneak.isEnabled) {
            isSneakToggled = !isSneakToggled

            if (!isSneakToggled) {
                KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, GameSettings.isKeyDown(minecraft.gameSettings.keyBindSneak))
            }
        }

        updateToggleSprintSneakStates(minecraft)
    }
}