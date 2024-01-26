package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.GUIUtils
import java.text.SimpleDateFormat
import java.util.*

object RealTimeTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun realTimeTracker() {
        if (!guiElements.realTimeTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf<String>()

        if (guiElements.realTimeTracker.includeDate) textArray.add(GUIUtils.primaryChars + SimpleDateFormat("E d/M/y").format(Date()))

        when (guiElements.realTimeTracker.timeFormat) {
            0 -> textArray.add(GUIUtils.primaryChars + SimpleDateFormat("h:mm:ss a").format(Date()))
            1 -> textArray.add(GUIUtils.primaryChars + SimpleDateFormat("HH:mm:ss").format(Date()))
            else -> {}
        }

        if (guiElements.realTimeTracker.includeTimezone) {
            textArray.add("${GUIUtils.primaryChars + SimpleDateFormat("z").format(Date())} (UTC ${SimpleDateFormat("XXX").format(Date())})")
        }

        RenderGuiData.renderElement(
            guiElements.realTimeTracker.coreSettings.x,
            guiElements.realTimeTracker.coreSettings.y,
            guiElements.realTimeTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}