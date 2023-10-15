package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun playerMotionTracker() {
        if (!guiElements.playerMotionElement.isEnabled) return

        val textArray = arrayOf(
            "§fMotion X: ${Minecraft.getMinecraft().thePlayer.motionX.round(guiElements.playerMotionElement.otherSettings.decimals)}",
            "§fMotion Y: ${Minecraft.getMinecraft().thePlayer.motionY.round(guiElements.playerMotionElement.otherSettings.decimals)}",
            "§fMotion Z: ${Minecraft.getMinecraft().thePlayer.motionZ.round(guiElements.playerMotionElement.otherSettings.decimals)}",
        )

        RenderGuiData.renderElement(
            guiElements.playerMotionElement.x,
            guiElements.playerMotionElement.y,
            guiElements.playerMotionElement.anchorCorner,
            textArray
        )
    }
}