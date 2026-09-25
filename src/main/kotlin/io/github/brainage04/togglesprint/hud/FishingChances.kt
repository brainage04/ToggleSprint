package io.github.brainage04.togglesprint.hud

/**
 * The catch-category chances of Forge's `FishingHooks.getFishableCategory`: junk is
 * `0.1 - 0.025 * luck - 0.01 * lure` and treasure `0.05 + 0.01 * luck - 0.01 * lure`, each clamped to
 * `[0, 1]`, and fish takes the rest. Worked in thousandths so the rule's steps are exact.
 */
object FishingChances {
    /** The `Chances: Fish f%, Treasure t%, Junk j%` line for Luck of the Sea [luck] and Lure [lure]. */
    fun line(luck: Int, lure: Int): String {
        val (fish, treasure, junk) = percentages(luck, lure)
        return "Chances: Fish $fish%, Treasure $treasure%, Junk $junk%"
    }

    /** Whole fish, treasure and junk percentages, largest-remainder rounded so they always add up to 100. */
    fun percentages(luck: Int, lure: Int): IntArray {
        val junk = (100 - 25 * luck - 10 * lure).coerceIn(0, 1000)
        val treasure = (50 + 10 * luck - 10 * lure).coerceIn(0, 1000 - junk)
        val fish = 1000 - junk - treasure
        val thousandths = intArrayOf(fish, treasure, junk)

        val percentages = IntArray(thousandths.size) { thousandths[it] / 10 }
        // hand the points lost to rounding down to the largest remainders, earlier categories first on ties
        val byRemainder = thousandths.indices.sortedByDescending { thousandths[it] % 10 }
        repeat(100 - percentages.sum()) { percentages[byRemainder[it]]++ }
        return percentages
    }
}
