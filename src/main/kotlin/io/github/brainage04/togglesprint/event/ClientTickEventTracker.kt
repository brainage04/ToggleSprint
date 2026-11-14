package io.github.brainage04.togglesprint.event

import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSneakToggled
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker.isSprintToggled
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.KeyBindingUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

class ClientTickEventTracker {
    companion object {
        fun updateToggleSprintSneakStates(minecraft: Minecraft) {
            if (!ConfigUtils.toggleMovementKeys.toggleSprint.isEnabled) isSprintToggled = false
            if (!ConfigUtils.toggleMovementKeys.toggleSneak.isEnabled) isSneakToggled = false

            // sprint handling
            if (isSprintToggled && !minecraft.gameSettings.keyBindSprint.isKeyDown && minecraft.currentScreen == null) KeyBindingUtils.setPressed(minecraft.gameSettings.keyBindSprint, true)

            // sneak handling
            if (minecraft.currentScreen == null) {
                if (isSneakToggled && !minecraft.gameSettings.keyBindSneak.isKeyDown) KeyBindingUtils.setPressed(minecraft.gameSettings.keyBindSneak, true)
            } else {
                if (minecraft.gameSettings.keyBindSneak.isKeyDown) KeyBindingUtils.setPressed(minecraft.gameSettings.keyBindSneak, false)
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