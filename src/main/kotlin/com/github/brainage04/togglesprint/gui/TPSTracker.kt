package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.MOD_ID
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import kotlin.concurrent.fixedRateTimer

object TPSTracker {
    private const val minDataAmount = 5
    private const val waitAfterWorldSwitch = 5

    private var packetsFromLastSecond = 0
    private var tickTimes = mutableListOf<Int>()
    private var ignoreFirstTicks = waitAfterWorldSwitch
    var hasPacketReceived = false

    var display: String = ""

    private fun getColor(tps: Double): String {
        return when {
            tps > 19 -> "§2"
            tps > 18 -> "§a"
            tps > 15 -> "§6"
            tps > 10 -> "§c"
            else -> "§4"
        }
    }

    init {
        fixedRateTimer(name = "skyhanni-tps-counter-seconds", period = 1_000L) {
            if (!guiElements.tpsTracker.coreSettings.isEnabled) return@fixedRateTimer
            if (packetsFromLastSecond == 0) return@fixedRateTimer

            if (ignoreFirstTicks > 0) {
                ignoreFirstTicks--
                packetsFromLastSecond = 0

                display = "§fTPS: Waiting... (${ignoreFirstTicks + minDataAmount}s)"

                return@fixedRateTimer
            }

            tickTimes.add(packetsFromLastSecond)
            packetsFromLastSecond = 0
            if (tickTimes.size > 10) tickTimes = tickTimes.drop(1).toMutableList()

            display = if (tickTimes.size < guiElements.tpsTracker.rangeInSeconds) "§fTPS: Waiting... (${guiElements.tpsTracker.rangeInSeconds - tickTimes.size}s)"
            else {
                val sum = tickTimes.sum().toDouble()
                val tps = (sum / tickTimes.size).round(1)

                "§fTPS: ${(getColor(tps)) + tps}"
            }
        }

        fixedRateTimer(name = "${MOD_ID}-tps-counter-ticks", period = 50L) {
            if (!guiElements.tpsTracker.coreSettings.isEnabled) return@fixedRateTimer

            if (hasPacketReceived) {
                hasPacketReceived = false
                packetsFromLastSecond++
            }
        }
    }

    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun tpsTracker() {
        if (!guiElements.tpsTracker.coreSettings.isEnabled) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        RenderGuiData.renderElement(
            guiElements.tpsTracker.coreSettings.x,
            guiElements.tpsTracker.coreSettings.y,
            guiElements.tpsTracker.coreSettings.anchorCorner,
            display,
        )
    }
}