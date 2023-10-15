package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerPositionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun playerPositionTracker() {
        if (!guiElements.playerPositionElement.coreSettings.isEnabled) return

        val textArray = arrayOf(
            "§fX: ${Minecraft.getMinecraft().thePlayer.posX.round(guiElements.playerPositionElement.decimals)}",
            "§fY: ${Minecraft.getMinecraft().thePlayer.posY.round(guiElements.playerPositionElement.decimals)}",
            "§fZ: ${Minecraft.getMinecraft().thePlayer.posZ.round(guiElements.playerPositionElement.decimals)}",
        )

        RenderGuiData.renderElement(
            guiElements.playerPositionElement.coreSettings.x,
            guiElements.playerPositionElement.coreSettings.y,
            guiElements.playerPositionElement.coreSettings.anchorCorner,
            textArray
        )
    }
}