package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

object PingTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun getColor(ping: Long): String {
        return when {
            ping < 50L -> "§2"
            ping < 100L -> "§a"
            ping < 200L -> "§6"
            ping < 300L -> "§c"
            else -> "§4"
        }
    }

    private var ping = 0L

    fun pingTracker() {
        if (!guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        ping =  0L
        if (!minecraft.isSingleplayer) {
            val currentServerData = minecraft.currentServerData ?: return
            ping = currentServerData.pingToServer
        }

        val text = if (guiElements.pingTracker.showColor) "§fPing: ${getColor(ping) + ping}ms"
        else "§fPing: ${ping}ms"

        RenderGuiData.renderElement(
            guiElements.pingTracker.coreSettings.x,
            guiElements.pingTracker.coreSettings.y,
            guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }
}