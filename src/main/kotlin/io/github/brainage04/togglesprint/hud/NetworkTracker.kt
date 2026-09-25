package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ChatUtils
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.ArrayDeque

/**
 * Ping and server TPS. The ping is the latency the server reports for the player in the player list,
 * sampled every few ticks and averaged; the TPS is measured from the game time the server reports
 * (S03PacketTimeUpdate) and capped at 20.
 */
object NetworkTracker {
    private const val MAX_TPS = 20.0

    private val tickRate = TickRateTracker()
    private val pingSamples = ArrayDeque<Long>()
    private var pingSum = 0L
    private var ticksSincePingSample = 0

    /**
     * Called on the network thread when the server reports its world's total game time; the report is
     * timestamped on arrival and recorded on the client thread, which is the only thread touching [tickRate].
     */
    @JvmStatic
    fun onServerGameTime(totalWorldTime: Long) {
        val receivedNanos = System.nanoTime()
        Minecraft.getMinecraft().addScheduledTask(Runnable {
            tickRate.record(totalWorldTime, receivedNanos, ConfigUtils.guiElements.networkTracker.tpsIntervalsTracked.coerceIn(1, 30))
        })
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return
        val config = ConfigUtils.guiElements.networkTracker

        if (++ticksSincePingSample < config.updatePingTickInterval.coerceIn(1, 20)) return
        ticksSincePingSample = 0

        val ping = minecraft.netHandler?.getPlayerInfo(player.uniqueID)?.responseTime ?: return
        pingSamples.addLast(ping.toLong())
        pingSum += ping
        while (pingSamples.size > config.pingIntervalsTracked.coerceIn(1, 30)) pingSum -= pingSamples.removeFirst()
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (!event.world.isRemote) return
        tickRate.reset()
        pingSamples.clear()
        pingSum = 0L
    }

    fun lines(): List<String> {
        if (Minecraft.getMinecraft().thePlayer == null) return emptyList()
        val config = ConfigUtils.guiElements.networkTracker
        val lines = ArrayList<String>(2)

        if (config.showPing) {
            val ping = if (pingSamples.isEmpty()) 0L else Math.round(pingSum.toDouble() / pingSamples.size)
            lines.add("Ping: ${if (config.colourValues) pingColour(ping) else ""}${ping}ms")
        }

        if (config.showTps) {
            if (!tickRate.hasRate()) {
                lines.add("TPS: -")
            } else {
                // network jitter can make a healthy server appear to tick slightly faster than its target
                val tps = tickRate.ticksPerSecond().coerceAtMost(MAX_TPS)
                lines.add("TPS: ${if (config.colourValues) tpsColour(tps) else ""}${roundDecimalPlaces(tps, config.tpsDecimalPlaces)}")
            }
        }

        return lines
    }

    private fun pingColour(ping: Long): String = when {
        ping < 50L -> ChatUtils.darkGreenChar
        ping < 100L -> ChatUtils.greenChar
        ping < 200L -> ChatUtils.yellowChar
        ping < 300L -> ChatUtils.redChar
        else -> ChatUtils.darkRedChar
    }

    private fun tpsColour(tps: Double): String = when {
        tps > 19.0 -> ChatUtils.darkGreenChar
        tps > 18.0 -> ChatUtils.greenChar
        tps > 15.0 -> ChatUtils.goldChar
        tps > 10.0 -> ChatUtils.redChar
        else -> ChatUtils.darkRedChar
    }
}
