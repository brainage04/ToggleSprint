package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ToggleSneakKeybind : KeyBinding("Toggle Sneak", Keyboard.KEY_RSHIFT, ToggleSprintMain.MOD_NAME) {
    companion object {
        var isToggled = false
        val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (!toggleMovementKeys.toggleSneak) return

            if (this.isPressed) {
                isToggled = !isToggled

                if (!isToggled && !Minecraft.getMinecraft().gameSettings.keyBindSneak.isPressed) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, false)
            }

            if (isToggled) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, true)

            if (Minecraft.getMinecraft().currentScreen != null) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, false) // disable toggle sneak in GUIs (sneaking in GUIs is illegal on most servers)
        }
    }
}