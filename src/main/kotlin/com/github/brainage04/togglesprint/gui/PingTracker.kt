package com.github.brainage04.togglesprint.gui

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

    fun lines(): List<String> {
        val minecraft = Minecraft.getMinecraft() ?: return emptyList()
        val player = minecraft.thePlayer ?: return emptyList()

        ping = if (minecraft.isSingleplayer) {
            0L
        } else {
            minecraft.netHandler?.getPlayerInfo(player.uniqueID)?.responseTime?.toLong() ?: return emptyList()
        }

        return listOf(
            if (ConfigUtils.guiElements.pingTracker.showColor) "Ping: ${getColor(ping) + ping}ms"
            else "Ping: ${ping}ms"
        )
    }
}