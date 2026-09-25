package io.github.brainage04.togglesprint.util

import java.util.Locale

/**
 * Minecraft-free enchantment logic, generic over the enchantment type so that it can be unit tested
 * without bootstrapping the game. [EnchantmentUtils] binds it to 1.8.9's enchantments.
 */
object EnchantmentConflicts {
    /**
     * The enchantments that could still be added to an item: applicable to it, not already present,
     * not blacklisted, and compatible with every enchantment already on it. [all]'s order is kept.
     */
    fun <E> missing(
        all: List<E>,
        present: Collection<E>,
        isApplicable: (E) -> Boolean,
        isBlacklisted: (E) -> Boolean,
        areCompatible: (E, E) -> Boolean,
    ): List<E> = all.filter { enchantment ->
        enchantment !in present &&
            !isBlacklisted(enchantment) &&
            isApplicable(enchantment) &&
            present.all { areCompatible(it, enchantment) }
    }

    /**
     * Groups enchantments that cannot coexist, so that each group can be shown as a single "choose
     * one" line. Groups are connected components of the conflict relation, so an enchantment that
     * conflicts with nothing is a group of one. Groups are ordered by their first member, and each
     * group keeps the input order.
     */
    fun <E> group(enchantments: List<E>, areCompatible: (E, E) -> Boolean): List<List<E>> {
        val groups = mutableListOf<List<E>>()
        val grouped = BooleanArray(enchantments.size)

        for (start in enchantments.indices) {
            if (grouped[start]) continue

            val members = mutableListOf<Int>()
            val queue = ArrayDeque<Int>()
            grouped[start] = true
            queue.addLast(start)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                members.add(current)

                for (other in enchantments.indices) {
                    if (grouped[other] || areCompatible(enchantments[current], enchantments[other])) continue

                    grouped[other] = true
                    queue.addLast(other)
                }
            }

            groups.add(members.sorted().map { enchantments[it] })
        }

        return groups
    }

    /**
     * The enchantments whose name matches [query], ignoring case: the exact match alone if there is
     * one, otherwise every enchantment whose name contains the query, in [enchantments]' order.
     */
    fun <E> findMatches(enchantments: List<E>, query: String, name: (E) -> String): List<E> {
        val normalisedQuery = query.lowercase(Locale.ROOT)
        val potentialMatches = mutableListOf<E>()

        for (enchantment in enchantments) {
            val normalisedName = name(enchantment).lowercase(Locale.ROOT)
            if (normalisedName == normalisedQuery) return listOf(enchantment)
            if (normalisedName.contains(normalisedQuery)) potentialMatches.add(enchantment)
        }

        return potentialMatches
    }
}
