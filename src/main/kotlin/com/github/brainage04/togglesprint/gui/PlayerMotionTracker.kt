package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun formatMotion(motion: Double, axis: String): String {
        var returnString = "§fMotion $axis: ${(motion * 20 * 2).round(guiElements.motionTracker.decimals)} m/s" // for some reason this number is half as big as it should be???

        if (guiElements.motionTracker.showTrueMotion) {
            returnString += " (${(motion * 2).round(guiElements.motionTracker.decimals)} m/tick)"
        }

        return returnString
    }

    fun playerMotionTracker() {
        if (!guiElements.motionTracker.coreSettings.isEnabled) return

        val textArray = arrayOf(
            formatMotion(Minecraft.getMinecraft().thePlayer.motionX, "X"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionY, "Y"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionZ, "Z"),
        )

        RenderGuiData.renderElement(
            guiElements.motionTracker.coreSettings.x,
            guiElements.motionTracker.coreSettings.y,
            guiElements.motionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}