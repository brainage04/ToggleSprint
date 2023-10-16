package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

object TPSTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    val tickTimes = arrayListOf<Long>()

    fun tpsTracker() {
        if (!guiElements.tpsTracker.coreSettings.isEnabled) return

        if (Minecraft.getMinecraft().thePlayer == null) return

        val textArray = arrayListOf<String>()

        if (tickTimes.size < guiElements.tpsTracker.tickRange) textArray.add("§fTPS: loading... (${guiElements.tpsTracker.tickRange - tickTimes.size}/$guiElements.tpsTracker.tickRange ticks left)")
        else {
            val difference = (tickTimes[guiElements.tpsTracker.tickRange - 1] - tickTimes[0]).toInt()
            val estimatedTps = difference / guiElements.tpsTracker.tickRange

            val prefix = when {
                estimatedTps > 19 -> "§2"
                estimatedTps > 18 -> "§a"
                estimatedTps > 15 -> "§6"
                estimatedTps > 10 -> "§c"
                else -> "§4"
            }

            textArray.add("§fTPS: ${prefix + estimatedTps}")

            if (guiElements.tpsTracker.showDifference) {
                textArray.add("§f${guiElements.tpsTracker.tickRange} ticks, ${difference}ms")
            }

        }
        RenderGuiData.renderElement(
            guiElements.tpsTracker.coreSettings.x,
            guiElements.tpsTracker.coreSettings.y,
            guiElements.tpsTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}