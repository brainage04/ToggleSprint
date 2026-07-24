package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class TPSTracker {
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        val config = ConfigUtils.guiElements.tpsTracker
        val minecraft = Minecraft.getMinecraft()
        if (!config.coreSettings.isEnabled || minecraft.thePlayer == null || minecraft.theWorld == null) {
            sampler.reset()
            display = ""
            return
        }

        if (sampler.tick()) display = formatTps(sampler.tps, config.showColor)
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (!event.world.isRemote) return
        sampler.reset()
        display = ""
    }

    companion object {
        private val sampler = TpsSampler()
        private var display = ""

        @JvmStatic
        fun onPacketReceived() {
            sampler.recordPacket()
        }

        fun tpsTracker() {
            val config = ConfigUtils.guiElements.tpsTracker
            if (!config.coreSettings.isEnabled) return
            if (Minecraft.getMinecraft().thePlayer == null) return

            if (display.isEmpty()) display = formatTps(0.0, config.showColor)
            RenderGuiData.renderElement(
                config.coreSettings.x,
                config.coreSettings.y,
                config.coreSettings.anchorCorner,
                display,
            )
        }

        private fun formatTps(tps: Double, showColor: Boolean): String {
            val color = if (showColor) getColor(tps) else ""
            return "${ConfigUtils.primaryChars}TPS: $color${tps.round(1)}"
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
}