package io.github.brainage04.togglesprint.waypoint

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.moulberry.moulconfig.ChromaColour

/**
 * The built-in waypoint at 0, 63, 0 that every dimension has. It is not stored with the world's
 * waypoints: its colour and visibility live in the config, and it cannot be edited or deleted.
 */
object WorldCentre {
    const val NAME = "World Centre"

    /** The World Centre in [dimension], with the configured colour and visibility. */
    fun waypoint(dimension: Int): Waypoint {
        val config = ConfigUtils.brainageHudParity.waypoints
        val rgb = runCatching { ChromaColour.specialToChromaRGB(config.worldCentreColour) }.getOrDefault(0xFFFFFF) and 0xFFFFFF
        return Waypoint(NAME, 0, 63, 0, dimension, rgb, config.showWorldCentre)
    }

    fun setVisible(visible: Boolean) {
        ConfigUtils.brainageHudParity.waypoints.showWorldCentre = visible
        ToggleSprintMain.configManager.save()
    }
}
