package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun formatMotion(motion: Double, axis: String): String {
        var returnString = "§fMotion $axis: ${motion * 20} m/s"

        if (guiElements.playerMotionElement.showTrueMotion) {
            returnString += "($motion m/tick)"
        }

        return returnString
    }

    fun playerMotionTracker() {
        if (!guiElements.playerMotionElement.coreSettings.isEnabled) return

        val textArray = arrayOf(
            formatMotion(Minecraft.getMinecraft().thePlayer.motionX.round(guiElements.playerMotionElement.decimals), "X"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionY.round(guiElements.playerMotionElement.decimals), "Y"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionZ.round(guiElements.playerMotionElement.decimals), "Z")
        )

        RenderGuiData.renderElement(
            guiElements.playerMotionElement.coreSettings.x,
            guiElements.playerMotionElement.coreSettings.y,
            guiElements.playerMotionElement.coreSettings.anchorCorner,
            textArray
        )
    }
}