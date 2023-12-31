package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerRotationTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun formatYaw(yaw: Float): String {
        var returnString: String = if (yaw > 0.0) {
            "§fYaw: ${(((yaw + 180) % 360) - 180).round(guiElements.rotationTracker.decimals)}"
        } else {
            "§fYaw: ${(((yaw - 180) % 360) + 180).round(guiElements.rotationTracker.decimals)}"
        }

        if (guiElements.rotationTracker.showTrueYaw) {
            returnString += " (${yaw.round(guiElements.rotationTracker.decimals)})"
        }

        return returnString
    }

    fun playerRotationTracker() {
        if (!guiElements.rotationTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf(
            formatYaw(Minecraft.getMinecraft().thePlayer.rotationYaw),
            "§fPitch: ${Minecraft.getMinecraft().thePlayer.rotationPitch.round(guiElements.rotationTracker.decimals)}",
        )

        if (guiElements.rotationTracker.dependOnFarmingTool) {
            val currentEquippedItem = thePlayer.getCurrentEquippedItem() ?: return

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
            guiElements.rotationTracker.coreSettings.x,
            guiElements.rotationTracker.coreSettings.y,
            guiElements.rotationTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}