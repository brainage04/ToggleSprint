package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import java.text.SimpleDateFormat
import java.util.*

object RealTimeTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val globalGuiSettings get() = ToggleSprintMain.config.globalGuiSettings

    val primaryChars = ChatUtils.colourChars[globalGuiSettings.primaryColour] + ChatUtils.effectChars[globalGuiSettings.primaryEffect]

    fun realTimeTracker() {
        if (!guiElements.realTimeTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf<String>()

        if (guiElements.realTimeTracker.includeDate) textArray.add(primaryChars + SimpleDateFormat("E d/M/y").format(Date()))

        when (guiElements.realTimeTracker.dateFormat) {
            1 -> textArray.add(primaryChars + SimpleDateFormat("HH:mm:ss").format(Date()))
            2 -> textArray.add(primaryChars + SimpleDateFormat("h:mm:ss a").format(Date()))
            else -> {}
        }

        if (guiElements.realTimeTracker.includeTimezone) {
            textArray.add("${primaryChars + SimpleDateFormat("z").format(Date())} (UTC ${SimpleDateFormat("XXX").format(Date())})")
        }

        RenderGuiData.renderElement(
            guiElements.realTimeTracker.coreSettings.x,
            guiElements.realTimeTracker.coreSettings.y,
            guiElements.realTimeTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}