package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.GUIUtils
import net.minecraft.client.Minecraft

object PingTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun getColor(ping: Long): String {
        return when {
            ping < 50L -> ChatUtils.darkGreenChar
            ping < 100L -> ChatUtils.greenChar
            ping < 200L -> ChatUtils.yellowChar
            ping < 300L -> ChatUtils.redChar
            else -> ChatUtils.darkRedChar
        }
    }

    private var ping = 0L

    fun pingTracker() {
        if (!guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        ping = 0L
        if (!minecraft.isSingleplayer) {
            val currentServerData = minecraft.currentServerData ?: return
            ping = currentServerData.pingToServer
        }

        val text = if (guiElements.pingTracker.showColor) "${GUIUtils.primaryChars}Ping: ${getColor(ping) + ping}ms"
        else "${GUIUtils.primaryChars}Ping: ${ping}ms"

        RenderGuiData.renderElement(
            guiElements.pingTracker.coreSettings.x,
            guiElements.pingTracker.coreSettings.y,
            guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }
}