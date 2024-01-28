package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private fun formatMotion(motion: Double, axis: String): String {
        var returnString = "${ConfigUtils.primaryChars}Motion $axis: ${(motion * 20 * 2).round(ConfigUtils.guiElements.motionTracker.decimals)} m/s" // for some reason this number is half as big as it should be???

        if (ConfigUtils.guiElements.motionTracker.showTrueMotion) {
            returnString += " (${(motion * 2).round(ConfigUtils.guiElements.motionTracker.decimals)} m/tick)"
        }

        return returnString
    }

    fun playerMotionTracker() {
        if (!ConfigUtils.guiElements.motionTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf(
            formatMotion(Minecraft.getMinecraft().thePlayer.motionX, "X"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionY, "Y"),
            formatMotion(Minecraft.getMinecraft().thePlayer.motionZ, "Z"),
        )

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.motionTracker.coreSettings.x,
            ConfigUtils.guiElements.motionTracker.coreSettings.y,
            ConfigUtils.guiElements.motionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}