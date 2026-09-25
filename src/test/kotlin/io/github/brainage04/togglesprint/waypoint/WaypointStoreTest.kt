package io.github.brainage04.togglesprint.waypoint

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WaypointStoreTest {
    @Test
    fun `quick names fill the first gap ignoring case`() {
        val waypoints = listOf(waypoint("Waypoint 1"), waypoint("waypoint 3"), waypoint("Base"))

        assertEquals("Waypoint 2", WaypointStore.nextQuickName(waypoints))
        assertEquals("Waypoint 4", WaypointStore.nextQuickName(listOf(
            waypoint("Waypoint 1"), waypoint("Waypoint 2"), waypoint("WAYPOINT 3"))))
    }

    @Test
    fun `distances are metres below a kilometre then kilometres`() {
        assertEquals("12 m", WaypointRenderer.formatDistance(12.4))
        assertEquals("999 m", WaypointRenderer.formatDistance(999.4))
        assertEquals("1.0 km", WaypointRenderer.formatDistance(999.6))
        assertEquals("1.4 km", WaypointRenderer.formatDistance(1_432.0))
    }

    @Test
    fun `quoted names with spaces are one word`() {
        assertEquals(listOf("My Base", "1", "64", "-3"), WaypointsCommand.splitQuoted("\"My Base\" 1 64 -3"))
        assertEquals(listOf("Base"), WaypointsCommand.splitQuoted("Base"))
        assertNull(WaypointsCommand.splitQuoted("\"My Base"))
    }

    private fun waypoint(name: String) = Waypoint(name)
}
