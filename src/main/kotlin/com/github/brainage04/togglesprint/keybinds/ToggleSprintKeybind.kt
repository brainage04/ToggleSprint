package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ToggleSprintKeybind : KeyBinding("Toggle Sprint", Keyboard.KEY_RCONTROL, ToggleSprintMain.MOD_NAME) {
    companion object {
        var isToggled = true
        val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (!toggleMovementKeys.toggleSprint) return

            if (this.isPressed) isToggled = !isToggled

            if (isToggled) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.keyCode, true)
        }
    }
}