package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerRotationTracker {
    private fun formatYaw(yaw: Float): String {
        val positiveYaw = (((yaw + 180.0f) % 360.0f) - 180.0f).round(ConfigUtils.guiElements.rotationTracker.decimals)
        val negativeYaw = (((yaw - 180.0f) % 360.0f) + 180.0f).round(ConfigUtils.guiElements.rotationTracker.decimals)

        var returnString = if (yaw > 0.0f) {
            positiveYaw.toString()
        } else {
            negativeYaw.toString()
        }

        if (ConfigUtils.guiElements.rotationTracker.showTrueYaw && !(yaw > -180.0f && yaw < 180.0f)) {
            returnString += " (${yaw.round(ConfigUtils.guiElements.rotationTracker.decimals)})"
        }

        return returnString
    }

    fun playerRotationTracker() {
        if (!ConfigUtils.guiElements.rotationTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = if (ConfigUtils.guiElements.rotationTracker.compactFormat) {
            arrayListOf(
                "${ConfigUtils.primaryChars + formatYaw(Minecraft.getMinecraft().thePlayer.rotationYaw)} / ${Minecraft.getMinecraft().thePlayer.rotationPitch.round(ConfigUtils.guiElements.rotationTracker.decimals)}",
            )
        } else {
            arrayListOf(
                "${ConfigUtils.primaryChars}Yaw: ${formatYaw(Minecraft.getMinecraft().thePlayer.rotationYaw)}",
                "${ConfigUtils.primaryChars}Pitch: ${Minecraft.getMinecraft().thePlayer.rotationPitch.round(ConfigUtils.guiElements.rotationTracker.decimals)}",
            )
        }

        if (ConfigUtils.guiElements.rotationTracker.dependOnFarmingTool) {
            val currentEquippedItem = thePlayer.currentEquippedItem ?: return

            val itemName = currentEquippedItem.displayName

            val strings = arrayListOf(
                "Axe",
                "Hoe",
                "Chopper",
                "Dicer",
                "Cutter",
                "Knife",
            )

            var holdingFarmingTool = false

            // this covers all bases for the 10 main Skyblock tools + other garden axes and hoes
            for (string in strings) {
                if (itemName.contains(string, true)) {
                    holdingFarmingTool = true
                    break
                }
            }

            if (!holdingFarmingTool) return
        }

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.rotationTracker.coreSettings.x,
            ConfigUtils.guiElements.rotationTracker.coreSettings.y,
            ConfigUtils.guiElements.rotationTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}