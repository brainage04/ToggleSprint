package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerRotationTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun playerRotationTracker() {
        if (!guiElements.playerRotationElement.isEnabled) return

        val yaw = Minecraft.getMinecraft().thePlayer.rotationYaw
        val pitch = Minecraft.getMinecraft().thePlayer.rotationPitch.round(guiElements.playerRotationElement.otherSettings.decimals)

        val textArray = if (yaw > 0) {
            arrayOf(
                "§fYaw: ${(((yaw + 180) % 360) - 180).round(guiElements.playerRotationElement.otherSettings.decimals)} (${yaw.round(guiElements.playerRotationElement.otherSettings.decimals)})",
                "§fPitch: $pitch",
            )
        } else {
            arrayOf(
                "§fYaw: ${(((yaw - 180) % 360) + 180).round(guiElements.playerRotationElement.otherSettings.decimals)} (${yaw.round(guiElements.playerRotationElement.otherSettings.decimals)})",
                "§fPitch: $pitch",
            )
        }

        RenderGuiData.renderElement(
            guiElements.playerRotationElement.x,
            guiElements.playerRotationElement.y,
            guiElements.playerRotationElement.displayAnchor,
            textArray
        )
    }
}