package io.github.brainage04.togglesprint.util

import com.sun.management.OperatingSystemMXBean
import org.lwjgl.opengl.ARBTimerQuery
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GLContext
import java.lang.management.ManagementFactory

/**
 * Samples process CPU load and times each frame on the GPU without waiting for it: a timestamp query
 * is submitted when a frame starts rendering and another when it ends, and a pair is only read once
 * OpenGL reports it available. GPU usage is the GPU's time for a frame over the time between frames.
 * All OpenGL methods must be called from the render thread while a context is current.
 */
object PerformanceTiming {
    private const val QUERY_PAIRS = 4
    private const val NANOSECONDS_PER_MILLISECOND = 1_000_000.0

    private val operatingSystemBean = ManagementFactory.getOperatingSystemMXBean() as? OperatingSystemMXBean
    private val startQueryIds = IntArray(QUERY_PAIRS)
    private val endQueryIds = IntArray(QUERY_PAIRS)
    private val pairPending = BooleanArray(QUERY_PAIRS)

    private var queriesAllocated = false
    /** The pair whose start timestamp was submitted for the frame being rendered, or -1. */
    private var framePair = -1
    private var previousFrameStartNanos = -1L
    private var latestFrameMillis = Double.NaN
    private var latestGpuFrameMillis = Double.NaN

    /** Returns a normalized process CPU percentage, or null when the JVM cannot provide it yet. */
    fun processCpuPercent(): Double? {
        val processCpuLoad = operatingSystemBean?.processCpuLoad ?: return null
        if (processCpuLoad < 0.0 || processCpuLoad.isNaN()) return null
        return (processCpuLoad * 100.0).coerceIn(0.0, 100.0)
    }

    /** Call when a frame starts rendering: reads finished pairs and submits this frame's start timestamp. */
    fun beginFrame() {
        val now = System.nanoTime()
        if (previousFrameStartNanos >= 0L) latestFrameMillis = (now - previousFrameStartNanos) / NANOSECONDS_PER_MILLISECOND
        previousFrameStartNanos = now

        if (!isTimerQuerySupported()) return
        if (!queriesAllocated && !allocateQueries()) return

        try {
            pollCompletedPairs()
            framePair = (0 until QUERY_PAIRS).firstOrNull { !pairPending[it] } ?: -1
            if (framePair >= 0) ARBTimerQuery.glQueryCounter(startQueryIds[framePair], ARBTimerQuery.GL_TIMESTAMP)
        } catch (_: Throwable) {
            cleanupGpuQueries()
        }
    }

    /** Call when a frame has finished rendering: submits its end timestamp. */
    fun endFrame() {
        val pair = framePair
        if (!queriesAllocated || pair < 0) return
        framePair = -1

        try {
            ARBTimerQuery.glQueryCounter(endQueryIds[pair], ARBTimerQuery.GL_TIMESTAMP)
            pairPending[pair] = true
        } catch (_: Throwable) {
            cleanupGpuQueries()
        }
    }

    /** The GPU time of the most recently measured frame in milliseconds. */
    fun gpuFrameMillis(): Double? = if (latestGpuFrameMillis.isNaN()) null else latestGpuFrameMillis

    /** The share of the time between frames that the GPU spent on a frame, as a percentage. */
    fun gpuUsagePercent(): Double? {
        if (latestGpuFrameMillis.isNaN() || latestFrameMillis.isNaN() || latestFrameMillis <= 0.0) return null
        return (latestGpuFrameMillis / latestFrameMillis * 100.0).coerceIn(0.0, 100.0)
    }

    /** Deletes allocated query objects. Call from the render thread before its OpenGL context is destroyed. */
    fun cleanupGpuQueries() {
        if (!queriesAllocated) return
        try {
            for (queryId in startQueryIds + endQueryIds) {
                if (queryId != 0) GL15.glDeleteQueries(queryId)
            }
        } catch (_: Throwable) {
            // The client may be tearing down its render context; reset local state regardless.
        }
        java.util.Arrays.fill(startQueryIds, 0)
        java.util.Arrays.fill(endQueryIds, 0)
        java.util.Arrays.fill(pairPending, false)
        queriesAllocated = false
        framePair = -1
        previousFrameStartNanos = -1L
        latestFrameMillis = Double.NaN
        latestGpuFrameMillis = Double.NaN
    }

    private fun isTimerQuerySupported(): Boolean {
        return try {
            GLContext.getCapabilities().GL_ARB_timer_query
        } catch (_: Throwable) {
            false
        }
    }

    private fun allocateQueries(): Boolean {
        return try {
            queriesAllocated = true
            for (index in 0 until QUERY_PAIRS) {
                startQueryIds[index] = GL15.glGenQueries()
                endQueryIds[index] = GL15.glGenQueries()
                if (startQueryIds[index] == 0 || endQueryIds[index] == 0) {
                    cleanupGpuQueries()
                    return false
                }
            }
            true
        } catch (_: Throwable) {
            cleanupGpuQueries()
            false
        }
    }

    private fun pollCompletedPairs() {
        for (index in 0 until QUERY_PAIRS) {
            if (!pairPending[index]) continue
            // the end timestamp was submitted after the start, so the start is available too
            if (GL15.glGetQueryObjecti(endQueryIds[index], GL15.GL_QUERY_RESULT_AVAILABLE) == 0) continue

            val start = ARBTimerQuery.glGetQueryObjectui64(startQueryIds[index], GL15.GL_QUERY_RESULT)
            val end = ARBTimerQuery.glGetQueryObjectui64(endQueryIds[index], GL15.GL_QUERY_RESULT)
            pairPending[index] = false
            if (end >= start) latestGpuFrameMillis = (end - start) / NANOSECONDS_PER_MILLISECOND
        }
    }
}
