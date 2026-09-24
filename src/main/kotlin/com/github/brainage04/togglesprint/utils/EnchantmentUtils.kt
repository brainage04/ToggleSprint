package com.github.brainage04.togglesprint.utils

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector
import java.util.Locale

/** 1.8.9 bindings for the enchantment information HUD, tooltip and commands. */
object EnchantmentUtils {
    // enchantments are registered during class and mod initialisation, before any of these are used
    private val idsByEnchantment: Map<Enchantment, String> by lazy {
        Enchantment.func_181077_c()
            .mapNotNull { location -> Enchantment.getEnchantmentByLocation(location.toString())?.let { it to location.toString() } }
            .toMap()
    }

    /** Every registered enchantment, in ID order so that output order is stable. */
    val all: List<Enchantment> by lazy { idsByEnchantment.keys.sortedBy { it.effectId } }

    /** The identifier used by the enchantment blacklist, e.g. `minecraft:sharpness`. */
    fun id(enchantment: Enchantment): String = idsByEnchantment[enchantment] ?: enchantment.name

    fun name(enchantment: Enchantment): String = StatCollector.translateToLocal(enchantment.name)

    /** The name with its level, leaving out the level of an enchantment that only has one. */
    fun fullName(enchantment: Enchantment, level: Int): String =
        if (level == 1 && enchantment.maxLevel == 1) name(enchantment) else enchantment.getTranslatedName(level)

    /** The name as shown in chat: grey, then reset so that following text is unaffected. */
    fun chatName(enchantment: Enchantment): String = "${ChatUtils.grayChar}${name(enchantment)}${ChatUtils.resetChar}"

    fun chatName(enchantment: Enchantment, level: Int): String =
        "${ChatUtils.grayChar}${fullName(enchantment, level)}${ChatUtils.resetChar}"

    fun areCompatible(first: Enchantment, second: Enchantment): Boolean =
        first.canApplyTogether(second) && second.canApplyTogether(first)

    fun isBlacklisted(enchantment: Enchantment): Boolean =
        id(enchantment) in ConfigUtils.brainageHudParity.enchantInfo.blacklistedEnchantmentIds

    /** The enchantments on [stack] (stored enchantments for enchanted books) in ID order, with their levels. */
    fun present(stack: ItemStack): Map<Enchantment, Int> =
        EnchantmentHelper.getEnchantments(stack).entries
            .mapNotNull { (id, level) -> Enchantment.getEnchantmentById(id)?.let { it to level } }
            .sortedBy { it.first.effectId }
            .toMap(LinkedHashMap())

    /** The enchantments that could still be added to [stack]; see [EnchantmentConflicts.missing]. */
    fun missing(stack: ItemStack, present: Collection<Enchantment>): List<Enchantment> =
        EnchantmentConflicts.missing(all, present, { it.canApply(stack) }, ::isBlacklisted, ::areCompatible)

    fun groupConflicting(enchantments: List<Enchantment>): List<List<Enchantment>> =
        EnchantmentConflicts.group(enchantments, ::areCompatible)

    /** The enchantment at its maximum level, followed by the level already on [stack]. */
    fun describeFor(enchantment: Enchantment, stack: ItemStack): String {
        val maxName = chatName(enchantment, enchantment.maxLevel)
        val currentLevel = EnchantmentHelper.getEnchantmentLevel(enchantment.effectId, stack)

        return when {
            currentLevel <= 0 -> maxName
            currentLevel == enchantment.maxLevel -> "You already have $maxName"
            else -> "$maxName - you have ${chatName(enchantment, currentLevel)}"
        }
    }

    /** The enchantment with this ID, with or without the `minecraft:` namespace, ignoring case. */
    fun byId(query: String): Enchantment? {
        val normalised = query.lowercase(Locale.ROOT)
        return all.firstOrNull { id(it) == normalised || id(it).substringAfter(':') == normalised }
    }

    /** See [EnchantmentConflicts.findMatches]. */
    fun findMatches(query: String): List<Enchantment> = EnchantmentConflicts.findMatches(all, query, ::name)
}
