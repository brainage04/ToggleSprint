package io.github.brainage04.togglesprint.event

import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import io.github.brainage04.togglesprint.event.ClientTickEventTracker.Companion.updateToggleSprintSneakStates
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSneakToggled
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSprintToggled
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.KeyBindingUtils
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
                release(minecraft.gameSettings.keyBindSprint)
            }
        }

        // sneak handling
        if (toggleSneakKeybind.isPressed && ConfigUtils.toggleMovementKeys.toggleSneak.isEnabled) {
            isSneakToggled = !isSneakToggled

            if (!isSneakToggled) {
                release(minecraft.gameSettings.keyBindSneak)
            }
        }

        updateToggleSprintSneakStates(minecraft)
    }

    private fun release(binding: KeyBinding) {
        KeyBindingUtils.setPressed(binding, false)
        // A physically held key goes back to the binding vanilla's key lookup gives it, which releases it on key up.
        KeyBinding.setKeyBindState(binding.keyCode, GameSettings.isKeyDown(binding))
    }
}