package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.utils.ConfigUtils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object RealTimeTracker {
    private val DATE = DateTimeFormatter.ofPattern("E d/M/y")
    private val TIME_12_HOUR = DateTimeFormatter.ofPattern("h:mm:ss a")
    private val TIME_24_HOUR = DateTimeFormatter.ofPattern("HH:mm:ss")
    // "xxx" always prints an offset such as +00:00, whereas "XXX" prints "Z" for UTC
    private val TIMEZONE = DateTimeFormatter.ofPattern("z '(UTC 'xxx')'")

    fun lines(): List<String> {
        val config = ConfigUtils.guiElements.realTimeTracker
        // one timestamp for every line, so the date and time cannot straddle midnight
        val now = ZonedDateTime.now()
        val textArray = ArrayList<String>(3)

        if (config.includeDate) textArray.add(DATE.format(now))

        when (config.timeFormat) {
            0 -> textArray.add(TIME_12_HOUR.format(now))
            1 -> textArray.add(TIME_24_HOUR.format(now))
            else -> {}
        }

        if (config.includeTimezone) textArray.add(TIMEZONE.format(now))

        return textArray
    }
}
