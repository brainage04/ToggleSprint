package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSneakKeybind
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ToggleSneakKeybind : KeyBinding("Toggle Sneak", Keyboard.KEY_RSHIFT, ToggleSprintMain.MOD_NAME) {
    companion object {
        var isToggled = false
        val sneak: ToggleMovementKeys.ToggleSneakCategory get() = ToggleSprintMain.config.toggleMovementKeys.toggleSneakCategory
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (!sneak.toggleSneak) return

            if (toggleSneakKeybind.isPressed) {
                isToggled = !isToggled

                if (!isToggled) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, false)
            }

            if (isToggled) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, true)

            if (Minecraft.getMinecraft().currentScreen != null) setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, false) // disable toggle sneak in GUIs (sneaking in GUIs is illegal on most servers)
        }
    }
}