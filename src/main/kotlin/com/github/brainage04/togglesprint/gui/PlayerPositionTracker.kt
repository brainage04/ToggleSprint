package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.Main
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

class PlayerPositionTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = Main.config.guiElements.playerPositionElement

        fun playerPositionTracker() {
            if (!config.isEnabled) return

            val textArray = arrayOf(
                "§fX: ${Minecraft.getMinecraft().thePlayer.posX.round(config.otherSettings.decimals)}",
                "§fY: ${Minecraft.getMinecraft().thePlayer.posY.round(config.otherSettings.decimals)}",
                "§fZ: ${Minecraft.getMinecraft().thePlayer.posZ.round(config.otherSettings.decimals)}",
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