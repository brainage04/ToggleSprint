package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import java.text.SimpleDateFormat
import java.util.*

object RealTimeTracker {
    fun realTimeTracker() {
        if (!ConfigUtils.guiElements.realTimeTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf<String>()

        if (ConfigUtils.guiElements.realTimeTracker.includeDate) textArray.add(ConfigUtils.primaryChars + SimpleDateFormat("E d/M/y").format(Date()))

        when (ConfigUtils.guiElements.realTimeTracker.timeFormat) {
            0 -> textArray.add(ConfigUtils.primaryChars + SimpleDateFormat("h:mm:ss a").format(Date()))
            1 -> textArray.add(ConfigUtils.primaryChars + SimpleDateFormat("HH:mm:ss").format(Date()))
            else -> {}
        }

        if (ConfigUtils.guiElements.realTimeTracker.includeTimezone) {
            textArray.add("${ConfigUtils.primaryChars + SimpleDateFormat("z").format(Date())} (UTC ${SimpleDateFormat("XXX").format(Date())})")
        }

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.realTimeTracker.coreSettings.x,
            ConfigUtils.guiElements.realTimeTracker.coreSettings.y,
            ConfigUtils.guiElements.realTimeTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}