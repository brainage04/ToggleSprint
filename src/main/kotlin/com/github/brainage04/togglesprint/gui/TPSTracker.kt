package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/** Server TPS measured from the game time the server reports (S03PacketTimeUpdate), capped at 20. */
object TPSTracker {
    private val tickRate = TickRateTracker()

    /**
     * Called on the network thread when the server reports its world's total game time; the report is
     * timestamped on arrival and recorded on the client thread, which is the only thread touching [tickRate].
     */
    @JvmStatic
    fun onServerGameTime(totalWorldTime: Long) {
        val receivedNanos = System.nanoTime()
        Minecraft.getMinecraft().addScheduledTask(Runnable {
            tickRate.record(totalWorldTime, receivedNanos, ConfigUtils.guiElements.tpsTracker.intervalsTracked.coerceIn(1, 30))
        })
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (event.world.isRemote) tickRate.reset()
    }

    fun lines(): List<String> {
        if (Minecraft.getMinecraft().thePlayer == null) return emptyList()
        val tps = tickRate.ticksPerSecond().coerceAtMost(20.0)
        val value = if (tickRate.hasRate()) tps.round(1).toString() else "--"
        val colour = if (ConfigUtils.guiElements.tpsTracker.showColor && tickRate.hasRate()) getColour(tps) else ""
        return listOf("TPS: $colour$value")
    }

    private fun getColour(tps: Double): String {
        return when {
            tps > 19 -> "§2"
            tps > 18 -> "§a"
            tps > 15 -> "§6"
            tps > 10 -> "§c"
            else -> "§4"
        }
    }
}
