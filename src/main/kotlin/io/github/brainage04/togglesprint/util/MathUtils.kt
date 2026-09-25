package io.github.brainage04.togglesprint.util

import java.text.DecimalFormat

object MathUtils {
    /** The largest number of decimal places any HUD option accepts. */
    const val MAX_DECIMAL_PLACES = 6

    // one format per precision, built on first use; HUD formatting only runs on the client thread
    private val formats = arrayOfNulls<DecimalFormat>(MAX_DECIMAL_PLACES + 1)

    /** Formats [input] with exactly [decimalPlaces] fractional digits, clamped to 0..[MAX_DECIMAL_PLACES]. */
    fun roundDecimalPlaces(input: Double, decimalPlaces: Int): String {
        val places = decimalPlaces.coerceIn(0, MAX_DECIMAL_PLACES)
        val format = formats[places] ?: DecimalFormat().apply {
            isGroupingUsed = false
            minimumFractionDigits = places
            maximumFractionDigits = places
        }.also { formats[places] = it }

        return format.format(input)
    }
}
