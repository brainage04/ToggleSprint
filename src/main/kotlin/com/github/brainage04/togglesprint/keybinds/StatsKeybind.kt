package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.DisplaySizeTracker
import com.github.brainage04.togglesprint.utils.ChatUtils
import io.github.moulberry.moulconfig.observer.Property
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class StatsKeybind : KeyBinding("Stats Keybind", Keyboard.KEY_V, ToggleSprintMain.MOD_NAME) {
    val guiElements get() = ToggleSprintMain.config.guiElements

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (!guiElements.displaySizeElement.isEnabled) return

            val scaledResolution = ScaledResolution(Minecraft.getMinecraft())

            if (this.isPressed) {
                ChatUtils.messageToChat("Line 1 length: ${Minecraft.getMinecraft().renderManager.fontRenderer.getStringWidth(DisplaySizeTracker.textArray[0])}")
                when (guiElements.displaySizeElement.anchorCorner) {
                    Property.of(GUIElements.AnchorCorner.TOPLEFT) -> ChatUtils.messageToChat("Position (Anchored ${guiElements.displaySizeElement.anchorCorner.toString()}): ${guiElements.displaySizeElement.x}, ${guiElements.displaySizeElement.y}")
                    Property.of(GUIElements.AnchorCorner.TOPRIGHT) -> ChatUtils.messageToChat("Position (Anchored ${guiElements.displaySizeElement.anchorCorner.toString()}): ${scaledResolution.scaledWidth} - ${guiElements.displaySizeElement.x}, ${guiElements.displaySizeElement.y}")
                    Property.of(GUIElements.AnchorCorner.BOTTOMLEFT) -> ChatUtils.messageToChat("Position (Anchored ${guiElements.displaySizeElement.anchorCorner.toString()}): ${guiElements.displaySizeElement.x}, ${scaledResolution.scaledHeight} - ${guiElements.displaySizeElement.y}")
                    Property.of(GUIElements.AnchorCorner.BOTTOMRIGHT) -> ChatUtils.messageToChat("Position (Anchored ${guiElements.displaySizeElement.anchorCorner.toString()}): ${scaledResolution.scaledWidth} - ${guiElements.displaySizeElement.x}, ${scaledResolution.scaledHeight} - ${guiElements.displaySizeElement.y}")
                }
            }
        }
    }
}