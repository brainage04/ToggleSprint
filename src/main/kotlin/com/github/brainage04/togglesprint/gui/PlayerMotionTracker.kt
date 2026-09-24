package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerMotionTracker {
    private fun formatMotion(motion: Double, axis: String): String {
        var returnString = "Motion $axis: ${(motion * 20 * 2).round(ConfigUtils.guiElements.motionTracker.decimals)} m/s" // for some reason this number is half as big as it should be???

        if (ConfigUtils.guiElements.motionTracker.showTrueMotion) {
            returnString += " (${(motion * 2).round(ConfigUtils.guiElements.motionTracker.decimals)} m/tick)"
        }

        return returnString
    }

    fun lines(): List<String> {
        val player = Minecraft.getMinecraft().thePlayer ?: return emptyList()

        return listOf(
            formatMotion(player.motionX, "X"),
            formatMotion(player.motionY, "Y"),
            formatMotion(player.motionZ, "Z"),
        )
    }
}