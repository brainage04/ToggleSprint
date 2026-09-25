package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ConfigUtils
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object RealTimeTracker {
    private val DATE = DateTimeFormatter.ofPattern("E dd MMM yyyy")
    private val TIME_12_HOUR = DateTimeFormatter.ofPattern("hh:mm:ss a")
    private val TIME_24_HOUR = DateTimeFormatter.ofPattern("HH:mm:ss")
    // "xxx" always prints an offset such as +00:00, whereas "XXX" prints "Z" for UTC
    private val TIMEZONE = DateTimeFormatter.ofPattern("z '(UTC 'xxx')'")

    fun lines(): List<String> {
        val config = ConfigUtils.guiElements.realTimeTracker
        // one timestamp for every line, so the date and time cannot straddle midnight
        val now = ZonedDateTime.now()
        val textArray = ArrayList<String>(3)

        if (config.showDate) textArray.add(DATE.format(now))

        if (config.showTime) {
            textArray.add(
                if (config.twelveHourFormat) TIME_12_HOUR.format(now).replace("am", "AM").replace("pm", "PM")
                else TIME_24_HOUR.format(now)
            )
        }

        if (config.showTimezone) textArray.add(TIMEZONE.format(now))

        return textArray
    }
}
