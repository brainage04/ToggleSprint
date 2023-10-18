package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

class InputEventTracker {
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    companion object {
        var isSprintToggled = true
        var isSneakToggled = false
    }

    init {
        isSprintToggled = toggleMovementKeys.toggleSprint.defaultState
        isSneakToggled = toggleMovementKeys.toggleSneak.defaultState
    }

    @SubscribeEvent
    fun onInputEvent(event: InputEvent) {
        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        if (toggleMovementKeys.toggleSprint.isEnabled) {
            if (isSprintToggled) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, true)

            if (toggleSprintKeybind.isPressed) {
                isSprintToggled = !isSprintToggled

                if (!isSprintToggled) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, false)
            }
        }

        if (toggleMovementKeys.toggleSneak.isEnabled) {
            if (toggleSneakKeybind.isPressed) {
                isSneakToggled = !isSneakToggled

                if (!isSneakToggled) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, false) // release sneak when toggle sneak disabled
            }

            if (isSneakToggled) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, true)

            if (minecraft.currentScreen != null) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, false) // disable toggle sneak in GUIs (sneaking in GUIs is illegal on most servers)
        }
    }
}