package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.PerformanceTiming
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.ArrayList
import java.util.Locale

/** Client performance overlay. OpenGL timing is maintained on the render thread by [update]. */
object PerformanceHud {
    private const val SAMPLE_INTERVAL_NANOS = 250_000_000L
    private const val BYTES_PER_MEBIBYTE = 1024L * 1024L

    private var lastTextSampleNanos = Long.MIN_VALUE
    private var cachedFps = 0
    private var cachedRamUsedMiB = 0L
    private var cachedRamMaxMiB = 0L
    private var cachedCpuPercent: Double? = null
    private var cachedGpuFrameMillis: Double? = null

    /** Call once per overlay render; it only submits/polls non-blocking GPU timestamp queries. */
    fun update() {
        val performance = ConfigUtils.brainageHudParity.performance
        if (performance.coreSettings.isEnabled && performance.showGpuUsage) {
            PerformanceTiming.updateGpuFrameTime()
        } else {
            PerformanceTiming.cleanupGpuQueries()
        }
    }

    /** Renders the configured performance measurements. */
    fun performanceHud() {
        val performance = ConfigUtils.brainageHudParity.performance
        if (!performance.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.theWorld == null || minecraft.thePlayer == null) return

        update()
        sampleText(minecraft)

        val lines = ArrayList<String>(4)
        if (performance.showFps) lines.add("${ConfigUtils.primaryChars}FPS: $cachedFps")
        if (performance.showRamUsage) lines.add("${ConfigUtils.primaryChars}RAM: $cachedRamUsedMiB / $cachedRamMaxMiB MiB")
        if (performance.showCpuUsage) {
            val cpu = cachedCpuPercent
            lines.add(if (cpu == null) "${ConfigUtils.primaryChars}CPU: --" else "${ConfigUtils.primaryChars}CPU: ${formatOneDecimal(cpu)}%")
        }
        if (performance.showGpuUsage) {
            val frameTime = cachedGpuFrameMillis
            lines.add(if (frameTime == null) "${ConfigUtils.primaryChars}GPU: --" else "${ConfigUtils.primaryChars}GPU: ${formatOneDecimal(frameTime)} ms")
        }
        if (lines.isEmpty()) return

        RenderGuiData.renderElement(
            performance.coreSettings.x,
            performance.coreSettings.y,
            performance.coreSettings.anchorCorner,
            lines,
        )
    }

    /** Releases GPU query objects while the render context is still current. */
    fun cleanup() {
        PerformanceTiming.cleanupGpuQueries()
        lastTextSampleNanos = Long.MIN_VALUE
        cachedGpuFrameMillis = null
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (event.world.isRemote) cleanup()
    }

    private fun sampleText(minecraft: Minecraft) {
        val now = System.nanoTime()
        if (now - lastTextSampleNanos < SAMPLE_INTERVAL_NANOS) return
        lastTextSampleNanos = now

        cachedFps = Minecraft.getDebugFPS()
        val runtime = Runtime.getRuntime()
        cachedRamUsedMiB = (runtime.totalMemory() - runtime.freeMemory()) / BYTES_PER_MEBIBYTE
        cachedRamMaxMiB = runtime.maxMemory() / BYTES_PER_MEBIBYTE
        cachedCpuPercent = PerformanceTiming.processCpuPercent()
        cachedGpuFrameMillis = PerformanceTiming.gpuFrameMillis()
    }

    private fun formatOneDecimal(value: Double): String = String.format(Locale.US, "%.1f", value)
}
