package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.isSneakToggled
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.isSprintToggled
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class ClientTickEventTracker {
    companion object {
        fun updateToggleSprintSneakStates(minecraft: Minecraft) {
            // sprint handling
            if (isSprintToggled && !minecraft.gameSettings.keyBindSprint.isKeyDown && minecraft.currentScreen == null) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.keyCode, true)

            // sneak handling
            if (minecraft.currentScreen == null) {
                if (isSneakToggled && !minecraft.gameSettings.keyBindSneak.isKeyDown) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, true)
            } else {
                if (minecraft.gameSettings.keyBindSneak.isKeyDown) KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.keyCode, false)
            }
        }
    }

    @SubscribeEvent
    fun onClientTickEvent(event: ClientTickEvent) {
        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        updateToggleSprintSneakStates(minecraft)
    }
}