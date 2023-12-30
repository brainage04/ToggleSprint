package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerPositionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun playerPositionTracker() {
        if (!guiElements.positionTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf(
            "§fX: ${Minecraft.getMinecraft().thePlayer.posX.round(guiElements.positionTracker.decimals)}",
            "§fY: ${Minecraft.getMinecraft().thePlayer.posY.round(guiElements.positionTracker.decimals)}",
            "§fZ: ${Minecraft.getMinecraft().thePlayer.posZ.round(guiElements.positionTracker.decimals)}",
        )

        RenderGuiData.renderElement(
            guiElements.positionTracker.coreSettings.x,
            guiElements.positionTracker.coreSettings.y,
            guiElements.positionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}