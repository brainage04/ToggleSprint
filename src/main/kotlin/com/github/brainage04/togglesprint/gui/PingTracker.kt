package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft

object PingTracker {
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
        if (!ConfigUtils.guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        ping = 0L
        if (!minecraft.isSingleplayer) {
            val currentServerData = minecraft.currentServerData ?: return
            ping = currentServerData.pingToServer
        }

        val text = if (ConfigUtils.guiElements.pingTracker.showColor) "${ConfigUtils.primaryChars}Ping: ${getColor(ping) + ping}ms"
        else "${ConfigUtils.primaryChars}Ping: ${ping}ms"

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.pingTracker.coreSettings.x,
            ConfigUtils.guiElements.pingTracker.coreSettings.y,
            ConfigUtils.guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }
}