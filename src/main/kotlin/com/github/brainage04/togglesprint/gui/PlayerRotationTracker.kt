package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

class PlayerRotationTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = ToggleSprintMain.config.guiElements.playerRotationElement

        fun playerRotationTracker() {
            if (!config.isEnabled) return

            val yaw = Minecraft.getMinecraft().thePlayer.rotationYaw
            val pitch = Minecraft.getMinecraft().thePlayer.rotationPitch.round(config.otherSettings.decimals)

            val textArray = arrayOf(
                "§fYaw: ${(((yaw + 180) % 360) - 180).round(config.otherSettings.decimals)} (${yaw.round(config.otherSettings.decimals)})",
                "§fPitch: $pitch",
            )

            RenderGuiData.renderElement(
                config.x,
                config.y,
                config.horizontalAlignment,
                config.verticalAlignment,
                textArray
            )
        }
    }
}