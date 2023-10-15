package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.toggleSprintKeybind
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import com.github.brainage04.togglesprint.gui.DisplaySizeTracker
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class StatsKeybind : KeyBinding("Toggle Sprint", Keyboard.KEY_V, ToggleSprintMain.MOD_NAME) {
    val guiElements get() = ToggleSprintMain.config.guiElements

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (!guiElements.displaySizeElement.isEnabled) return

            if (this.isPressed) {
                ChatUtils.messageToChat("Line 1 length: ${Minecraft.getMinecraft().fontRendererObj.getStringWidth(DisplaySizeTracker.textArray[0])}")
                ChatUtils.messageToChat("Line 2 length: ${Minecraft.getMinecraft().fontRendererObj.getStringWidth(DisplaySizeTracker.textArray[1])}")
            }
        }
    }
}