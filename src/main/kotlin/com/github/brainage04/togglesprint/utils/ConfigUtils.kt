package com.github.brainage04.togglesprint.utils

import com.github.brainage04.togglesprint.ToggleSprintMain

object ConfigUtils {
    val globalGuiSettings get() = ToggleSprintMain.config.globalGuiSettings
    val guiElements get() = ToggleSprintMain.config.guiElements
    val inventoryTrackers get() = ToggleSprintMain.config.inventoryTrackers
    val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys
    val primaryChars get() = ChatUtils.colourChars[globalGuiSettings.primaryColour] + ChatUtils.effectChars[globalGuiSettings.primaryEffect]
    val secondaryChars get() = ChatUtils.colourChars[globalGuiSettings.secondaryColour] + ChatUtils.effectChars[globalGuiSettings.secondaryEffect]
}