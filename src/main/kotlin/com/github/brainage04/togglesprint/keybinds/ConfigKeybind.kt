package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ConfigKeybind : KeyBinding("Open Config Menu", Keyboard.KEY_RETURN, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (this.isPressed) {
                ToggleSprintMain.configManager.openConfigGui()
            }
        }
    }
}