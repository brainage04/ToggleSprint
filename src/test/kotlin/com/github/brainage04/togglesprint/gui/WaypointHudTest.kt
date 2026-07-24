package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.config.categories.BrainageHudParity
import kotlin.test.Test
import kotlin.test.assertEquals

class WaypointHudTest {
    @Test
    fun `direction arrows are relative to Minecraft camera yaw`() {
        assertEquals("↑", WaypointHud.relativeArrow(0.0, 10.0, 0.0f))
        assertEquals("←", WaypointHud.relativeArrow(10.0, 0.0, 0.0f))
        assertEquals("↑", WaypointHud.relativeArrow(10.0, 0.0, -90.0f))
        assertEquals("→", WaypointHud.relativeArrow(0.0, 10.0, -90.0f))
    }

    @Test
    fun `waypoints are dimension-filtered and nearest-first`() {
        val near = waypoint("near", 3.0, 4.0, 0.0, 0)
        val far = waypoint("far", 10.0, 0.0, 0.0, 0)
        val otherDimension = waypoint("other", 1.0, 0.0, 0.0, -1)
        val invalid = waypoint("invalid", Double.NaN, 0.0, 0.0, 0)

        val ordered = WaypointHud.orderedWaypoints(listOf(far, otherDimension, invalid, near), 0, 0.0, 0.0, 0.0)

        assertEquals(listOf(near, far), ordered)
        assertEquals(5L, WaypointHud.roundedDistance(near, 0.0, 0.0, 0.0))
    }

    private fun waypoint(name: String, x: Double, y: Double, z: Double, dimension: Int) =
        BrainageHudParity.Waypoint(name, x, y, z, dimension)
}
