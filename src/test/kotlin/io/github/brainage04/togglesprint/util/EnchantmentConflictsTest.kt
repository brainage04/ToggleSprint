package io.github.brainage04.togglesprint.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Ported from BrainageHUD's EnchantInfoHudTest and GetEnchantInfoCommandTest. 1.8.9 enchantments
 * cannot be used without bootstrapping the game, so these enchantments model the conflicts, items
 * and names that matter to the logic.
 */
class EnchantmentConflictsTest {
    private data class TestEnchantment(val name: String, val items: Set<String>, val exclusiveGroup: String? = null)

    private val sharpness = TestEnchantment("Sharpness", setOf("sword", "axe"), "damage")
    private val smite = TestEnchantment("Smite", setOf("sword", "axe"), "damage")
    private val baneOfArthropods = TestEnchantment("Bane of Arthropods", setOf("sword", "axe"), "damage")
    private val efficiency = TestEnchantment("Efficiency", setOf("pickaxe"))
    private val silkTouch = TestEnchantment("Silk Touch", setOf("pickaxe"), "loot")
    private val unbreaking = TestEnchantment("Unbreaking", setOf("sword", "axe", "pickaxe"))
    private val fortune = TestEnchantment("Fortune", setOf("pickaxe"), "loot")
    private val fireAspect = TestEnchantment("Fire Aspect", setOf("sword"))
    private val looting = TestEnchantment("Looting", setOf("sword"), "loot-sword")
    private val all = listOf(sharpness, smite, baneOfArthropods, efficiency, silkTouch, unbreaking, fortune, fireAspect, looting)

    private fun areCompatible(first: TestEnchantment, second: TestEnchantment) =
        first != second && (first.exclusiveGroup == null || first.exclusiveGroup != second.exclusiveGroup)

    private fun missing(item: String, present: List<TestEnchantment>, blacklist: Set<TestEnchantment> = emptySet()) =
        EnchantmentConflicts.missing(all, present, { item in it.items }, { it in blacklist }, ::areCompatible)

    @Test
    fun `enchantments conflicting with present ones and blacklisted ones are not missing`() {
        // smite and bane of arthropods conflict with the sharpness already on the sword
        assertEquals(
            listOf(unbreaking, looting),
            missing("sword", listOf(sharpness), blacklist = setOf(fireAspect)),
        )
    }

    @Test
    fun `an item with every available enchantment is missing nothing`() {
        assertEquals(emptyList(), missing("sword", listOf(sharpness, unbreaking, looting), blacklist = setOf(fireAspect)))
    }

    @Test
    fun `mutually exclusive enchantments share one group in input order`() {
        assertEquals(
            listOf(listOf(efficiency), listOf(silkTouch, fortune), listOf(unbreaking)),
            EnchantmentConflicts.group(missing("pickaxe", emptyList()), ::areCompatible),
        )
    }

    @Test
    fun `groups are connected by conflicts even when not every pair conflicts`() {
        // riptide in later versions: conflicts with both loyalty and channeling, which coexist
        val riptide = TestEnchantment("Riptide", emptySet())
        val loyalty = TestEnchantment("Loyalty", emptySet())
        val channeling = TestEnchantment("Channeling", emptySet())
        val impaling = TestEnchantment("Impaling", emptySet())
        val conflicts = setOf(setOf(riptide, loyalty), setOf(riptide, channeling))

        assertEquals(
            listOf(listOf(loyalty, riptide, channeling), listOf(impaling)),
            EnchantmentConflicts.group(listOf(loyalty, riptide, channeling, impaling)) { a, b -> setOf(a, b) !in conflicts },
        )
    }

    @Test
    fun `names match regardless of case`() {
        assertEquals(listOf(sharpness), findMatches("SHARPNESS"))
        assertEquals(listOf(fireAspect), findMatches("fire aspect"))
    }

    @Test
    fun `an exact match wins over partial matches`() {
        val protection = TestEnchantment("Protection", emptySet())
        val fireProtection = TestEnchantment("Fire Protection", emptySet())

        assertEquals(listOf(protection), EnchantmentConflicts.findMatches(listOf(fireProtection, protection), "protection") { it.name })
    }

    @Test
    fun `every partial match is listed in input order`() {
        assertEquals(listOf(unbreaking, looting), findMatches("ING"))
        assertEquals(emptyList(), findMatches("protection"))
    }

    private fun findMatches(query: String) = EnchantmentConflicts.findMatches(all, query) { it.name }
}
