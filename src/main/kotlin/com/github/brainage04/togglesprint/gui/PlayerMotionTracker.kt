package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun formatMotion(motion: Double, axis: String): String {
        var returnString = "§fMotion $axis: ${(motion * 20 * 2).round(guiElements.playerMotionElement.decimals)} m/s" // for some reason this number is half as big as it should be???

        if (guiElements.playerMotionElement.showTrueMotion) {
            returnString += "(${motion.round(guiElements.playerMotionElement.decimals)} m/tick)"
        }

        return returnString
    }

    fun playerMotionTracker() {
        if (!guiElements.playerMotionElement.coreSettings.isEnabled) return

        val textArray = arrayOf(
            formatMotion(Minecraft.getMinecraft().thePlayer.motionX, "X"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionY, "Y"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionZ, "Z"),
        )

        RenderGuiData.renderElement(
            guiElements.playerMotionElement.coreSettings.x,
            guiElements.playerMotionElement.coreSettings.y,
            guiElements.playerMotionElement.coreSettings.anchorCorner,
            textArray,
        )
    }
}