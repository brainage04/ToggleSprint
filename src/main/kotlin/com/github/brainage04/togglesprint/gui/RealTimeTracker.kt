package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import java.text.SimpleDateFormat
import java.util.*

object RealTimeTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun realTimeTracker() {
        if (!guiElements.realTimeTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf<String>()

        if (guiElements.realTimeTracker.includeDate) textArray.add("§f${SimpleDateFormat("E d MMMM y").format(Date())}")

        when (guiElements.realTimeTracker.dateFormat) {
            1 -> textArray.add("§f${SimpleDateFormat("h:mm:ss a").format(Date())}")
            2 -> textArray.add("§f${SimpleDateFormat("HH:mm:ss").format(Date())}")
            else -> {}
        }

        if (guiElements.realTimeTracker.includeTimezone) {
            textArray.add("§f${SimpleDateFormat("z").format(Date())} (UTC ${SimpleDateFormat("XXX").format(Date())})")
        }

        RenderGuiData.renderElement(
            guiElements.realTimeTracker.coreSettings.x,
            guiElements.realTimeTracker.coreSettings.y,
            guiElements.realTimeTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}