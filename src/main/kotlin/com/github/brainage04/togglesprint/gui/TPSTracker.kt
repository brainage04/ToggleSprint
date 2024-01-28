package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.events.core.PacketEvent
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class TPSTracker {
    companion object {
        private const val minDataAmount = 5
        private const val waitAfterWorldSwitch = 6

        var display = "${ConfigUtils.primaryChars}TPS: "

        fun tpsTracker() {
            if (!ConfigUtils.guiElements.tpsTracker.coreSettings.isEnabled) return
            if (Minecraft.getMinecraft().thePlayer == null) return

            RenderGuiData.renderElement(
                ConfigUtils.guiElements.tpsTracker.coreSettings.x,
                ConfigUtils.guiElements.tpsTracker.coreSettings.y,
                ConfigUtils.guiElements.tpsTracker.coreSettings.anchorCorner,
                display,
            )
        }
    }

    private var packetsFromLastSecond = 0
    private var tpsList = mutableListOf<Int>()
    private var ignoreFirstTicks = waitAfterWorldSwitch
    private var hasPacketReceived = false

    /*
    init {
        fixedRateTimer(name = "brainage04-togglesprint-tps-counter-seconds", period = 1_000L) {
            if (!guiElements.tpsTracker.coreSettings.isEnabled) return@fixedRateTimer
            if (packetsFromLastSecond == 0) return@fixedRateTimer

            if (ignoreFirstTicks > 0) {
                ignoreFirstTicks--
                val current = ignoreFirstTicks + minDataAmount
                display = "§eTPS: §f(${current}s)"
                packetsFromLastSecond = 0
                return@fixedRateTimer
            }

            tpsList.add(packetsFromLastSecond)
            packetsFromLastSecond = 0
            if (tpsList.size > 10) {
                tpsList = tpsList.drop(1).toMutableList()
            }

            display = if (tpsList.size < minDataAmount) {
                val current = minDataAmount - tpsList.size
                "§eTPS: §f(${current}s)"
            } else {
                val sum = tpsList.sum().toDouble()
                var tps = (sum / tpsList.size).round(1)
                if (tps > 20) tps = 20.0
                val color = getColor(tps)
                "§eTPS: $color$tps"
            }

            ChatUtils.messageToChat("TPS List: $tpsList", soundType = ChatUtils.SoundType.NOTIFICATION)
        }

        fixedRateTimer(name = "brainage04-togglesprint-tps-counter-ticks", period = 50L) {
            if (!guiElements.tpsTracker.coreSettings.isEnabled) return@fixedRateTimer

            if (hasPacketReceived) {
                hasPacketReceived = false
                packetsFromLastSecond++
            }
        }
    }
     */

    @SubscribeEvent
    fun onWorldChange(event: Event) {
        tpsList.clear()
        packetsFromLastSecond = 0
        ignoreFirstTicks = waitAfterWorldSwitch
        display = ""
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    fun onPacketEvent(event: PacketEvent.ReceiveEvent) {
        if (!ConfigUtils.guiElements.tpsTracker.coreSettings.isEnabled) return
        hasPacketReceived = true
    }

    private fun getColor(tps: Double): String {
        return when {
            tps > 19 -> "§2"
            tps > 18 -> "§a"
            tps > 15 -> "§6"
            tps > 10 -> "§c"
            else -> "§4"
        }
    }
}