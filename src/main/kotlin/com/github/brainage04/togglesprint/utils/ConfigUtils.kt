package com.github.brainage04.togglesprint.utils

import com.github.brainage04.togglesprint.ToggleSprintMain

object ConfigUtils {
    val globalGuiSettings get() = ToggleSprintMain.config.globalGuiSettings
    val guiElements get() = ToggleSprintMain.config.guiElements
    val inventoryTrackers get() = ToggleSprintMain.config.inventoryTrackers
    val brainageHudParity get() = ToggleSprintMain.config.brainageHudParity
    val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys
}