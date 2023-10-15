package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerRotationTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun formatYaw(yaw: Float): String {
        var returnString: String = if (yaw > 0.0) {
            "§fYaw: ${(((yaw + 180) % 360) - 180).round(guiElements.playerRotationElement.decimals)}"
        } else {
            "§fYaw: ${(((yaw - 180) % 360) + 180).round(guiElements.playerRotationElement.decimals)}"
        }

        if (guiElements.playerRotationElement.showTrueYaw) {
            returnString += " (${yaw.round(guiElements.playerRotationElement.decimals)})"
        }

        return returnString
    }

    fun playerRotationTracker() {
        if (!guiElements.playerRotationElement.coreSettings.isEnabled) return

        val textArray = arrayOf(
            formatYaw(Minecraft.getMinecraft().thePlayer.rotationYaw),
            "§fPitch: ${Minecraft.getMinecraft().thePlayer.rotationPitch.round(guiElements.playerRotationElement.decimals)}",
        )

        RenderGuiData.renderElement(
            guiElements.playerRotationElement.coreSettings.x,
            guiElements.playerRotationElement.coreSettings.y,
            guiElements.playerRotationElement.coreSettings.anchorCorner,
            textArray,
        )
    }
}