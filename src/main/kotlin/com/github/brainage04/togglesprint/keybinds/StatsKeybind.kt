package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class StatsKeybind : KeyBinding("Stats Keybind", Keyboard.KEY_V, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (this.isPressed) {
                ChatUtils.messageToChat("testing", ChatUtils.PrefixType.GREEN, ChatUtils.SoundType.NOTIFICATION)
            }
        }
    }
}