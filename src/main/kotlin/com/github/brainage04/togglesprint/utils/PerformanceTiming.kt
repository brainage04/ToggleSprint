package com.github.brainage04.togglesprint.utils

import com.sun.management.OperatingSystemMXBean
import org.lwjgl.opengl.ARBTimerQuery
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GLContext
import java.lang.management.ManagementFactory

/**
 * Samples process CPU load and GPU timestamp queries without waiting for the GPU.
 * All OpenGL methods must be called from the render thread while a context is current.
 */
object PerformanceTiming {
    private const val QUERY_COUNT = 4
    private const val NANOSECONDS_PER_MILLISECOND = 1_000_000.0

    private val operatingSystemBean = ManagementFactory.getOperatingSystemMXBean() as? OperatingSystemMXBean
    private val queryIds = IntArray(QUERY_COUNT)
    private val queryPending = BooleanArray(QUERY_COUNT)

    private var queriesAllocated = false
    private var nextQuery = 0
    private var previousTimestamp = -1L
    private var latestGpuFrameMillis = Double.NaN

    /** Returns a normalized process CPU percentage, or null when the JVM cannot provide it yet. */
    fun processCpuPercent(): Double? {
        val processCpuLoad = operatingSystemBean?.processCpuLoad ?: return null
        if (processCpuLoad < 0.0 || processCpuLoad.isNaN()) return null
        return (processCpuLoad * 100.0).coerceIn(0.0, 100.0)
    }

    /**
     * Polls older timestamp queries and submits one new timestamp query. Results are only read
     * once OpenGL reports availability, so this never waits for the GPU on the render path.
     */
    fun updateGpuFrameTime() {
        if (!isTimerQuerySupported()) return
        if (!queriesAllocated && !allocateQueries()) return

        try {
            pollCompletedQueries()
            submitTimestampQuery()
        } catch (_: Throwable) {
            cleanupGpuQueries()
        }
    }

    /** Returns the most recently completed GPU timestamp interval in milliseconds. */
    fun gpuFrameMillis(): Double? = if (latestGpuFrameMillis.isNaN()) null else latestGpuFrameMillis

    /** Deletes allocated query objects. Call from the render thread before its OpenGL context is destroyed. */
    fun cleanupGpuQueries() {
        try {
            for (queryId in queryIds) {
                if (queryId != 0) GL15.glDeleteQueries(queryId)
            }
        } catch (_: Throwable) {
            // The client may be tearing down its render context; reset local state regardless.
        }
        java.util.Arrays.fill(queryIds, 0)
        java.util.Arrays.fill(queryPending, false)
        queriesAllocated = false
        nextQuery = 0
        previousTimestamp = -1L
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
            for (index in queryIds.indices) {
                queryIds[index] = GL15.glGenQueries()
                if (queryIds[index] == 0) {
                    cleanupGpuQueries()
                    return false
                }
            }
            queriesAllocated = true
            true
        } catch (_: Throwable) {
            cleanupGpuQueries()
            false
        }
    }

    private fun pollCompletedQueries() {
        for (index in queryIds.indices) {
            if (!queryPending[index]) continue
            if (GL15.glGetQueryObjecti(queryIds[index], GL15.GL_QUERY_RESULT_AVAILABLE) == 0) continue

            val timestamp = ARBTimerQuery.glGetQueryObjectui64(queryIds[index], GL15.GL_QUERY_RESULT)
            queryPending[index] = false
            if (previousTimestamp >= 0L && timestamp >= previousTimestamp) {
                latestGpuFrameMillis = (timestamp - previousTimestamp) / NANOSECONDS_PER_MILLISECOND
            }
            previousTimestamp = timestamp
        }
    }

    private fun submitTimestampQuery() {
        for (offset in queryIds.indices) {
            val index = (nextQuery + offset) % QUERY_COUNT
            if (queryPending[index]) continue
            ARBTimerQuery.glQueryCounter(queryIds[index], ARBTimerQuery.GL_TIMESTAMP)
            queryPending[index] = true
            nextQuery = (index + 1) % QUERY_COUNT
            return
        }
    }
}
