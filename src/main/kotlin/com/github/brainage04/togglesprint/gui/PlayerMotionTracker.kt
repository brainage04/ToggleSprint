package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

class PlayerMotionTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = ToggleSprintMain.config.guiElements.playerMotionElement

        fun playerMotionTracker() {
            if (!config.isEnabled) return

            val textArray = arrayOf(
                "§fMotion X: ${Minecraft.getMinecraft().thePlayer.motionX.round(config.otherSettings.decimals)}",
                "§fMotion Y: ${Minecraft.getMinecraft().thePlayer.motionY.round(config.otherSettings.decimals)}",
                "§fMotion Z: ${Minecraft.getMinecraft().thePlayer.motionZ.round(config.otherSettings.decimals)}",
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