package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import io.github.brainage04.togglesprint.util.PerformanceTiming
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.ArrayList

/** Client performance overlay. GPU timing brackets each frame's rendering on the render thread. */
object PerformanceHud {
    private const val SAMPLE_INTERVAL_NANOS = 250_000_000L
    private const val BYTES_PER_MEGABYTE = 1_048_576L

    private var lastTextSampleNanos: Long? = null
    private var cachedFps = 0
    private var cachedRamLine = ""
    private var cachedCpuPercent = 0L
    private var cachedGpuFrameMillis: Double? = null
    private var cachedGpuPercent: Long? = null

    private fun isGpuTimingEnabled(): Boolean {
        val performance = ConfigUtils.brainageHudParity.performance
        return performance.coreSettings.isEnabled && (performance.showGpuUsage || performance.showGpuFrameTime)
    }

    /** Submits the GPU timestamps around each frame; they are only read once available, so this never waits. */
    @SubscribeEvent
    fun onRenderTick(event: TickEvent.RenderTickEvent) {
        if (!isGpuTimingEnabled()) {
            PerformanceTiming.cleanupGpuQueries()
            return
        }
        if (event.phase == TickEvent.Phase.START) PerformanceTiming.beginFrame() else PerformanceTiming.endFrame()
    }

    /** The configured performance measurements. */
    fun lines(): List<String> {
        val performance = ConfigUtils.brainageHudParity.performance
        val minecraft = Minecraft.getMinecraft() ?: return emptyList()
        if (minecraft.theWorld == null || minecraft.thePlayer == null) return emptyList()

        sampleText()

        val lines = ArrayList<String>(5)
        if (performance.showFps) lines.add("$cachedFps FPS")
        if (performance.showRamUsage) lines.add(cachedRamLine)
        if (performance.showGpuUsage) lines.add("GPU: ${cachedGpuPercent?.let { "$it%" } ?: "-"}")
        if (performance.showGpuFrameTime) lines.add("GPU Time: ${cachedGpuFrameMillis?.let { "${roundDecimalPlaces(it, 1)} ms" } ?: "-"}")
        if (performance.showCpuUsage) lines.add("CPU: $cachedCpuPercent%")
        return lines
    }

    /** Releases GPU query objects while the render context is still current. */
    fun cleanup() {
        PerformanceTiming.cleanupGpuQueries()
        lastTextSampleNanos = null
        cachedGpuFrameMillis = null
        cachedGpuPercent = null
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (event.world.isRemote) cleanup()
    }

    private fun sampleText() {
        val now = System.nanoTime()
        val last = lastTextSampleNanos
        if (last != null && now - last < SAMPLE_INTERVAL_NANOS) return
        lastTextSampleNanos = now

        cachedFps = Minecraft.getDebugFPS()
        val runtime = Runtime.getRuntime()
        val max = runtime.maxMemory()
        val used = runtime.totalMemory() - runtime.freeMemory()
        cachedRamLine = "RAM: ${used * 100L / max}% (${used / BYTES_PER_MEGABYTE}/${max / BYTES_PER_MEGABYTE}MB)"
        cachedCpuPercent = PerformanceTiming.processCpuPercent()?.toLong() ?: 0L
        cachedGpuFrameMillis = PerformanceTiming.gpuFrameMillis()
        cachedGpuPercent = PerformanceTiming.gpuUsagePercent()?.toLong()
    }
}
