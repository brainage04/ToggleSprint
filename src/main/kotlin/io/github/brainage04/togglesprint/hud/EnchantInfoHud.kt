package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.config.categories.BrainageHudParity
import io.github.brainage04.togglesprint.util.ChatUtils
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.EnchantmentUtils
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.StatCollector

/**
 * Displays the enchantments on the held item, followed by the enchantments that could still be
 * added to it. Enchantments that cannot coexist are collapsed onto a single "choose one" line, and
 * blacklisted enchantments are never shown as missing.
 */
object EnchantInfoHud {
    // the lines only change with their inputs, so they are rebuilt only when an input changes
    // rather than walking every enchantment each frame
    private var cachedLines: List<String>? = null
    private var cachedStack: ItemStack? = null
    private var cachedConfigFlags = 0
    private var cachedBlacklist: String? = null
    private var cachedTranslationTime = 0L

    fun lines(): List<String> {
        val player = Minecraft.getMinecraft().thePlayer ?: return emptyList()
        val stack: ItemStack? = player.heldItem
        val config = ConfigUtils.brainageHudParity.enchantInfoHud
        val configFlags = configFlags(config)
        val blacklist = ConfigUtils.brainageHudParity.enchantInfo.blacklistedEnchantmentIds
        val translationTime = StatCollector.getLastTranslationUpdateTimeInMilliseconds()

        val lines = cachedLines
        if (lines != null &&
            configFlags == cachedConfigFlags &&
            translationTime == cachedTranslationTime &&
            ItemStack.areItemStacksEqual(stack, cachedStack) &&
            blacklist == cachedBlacklist
        ) return lines

        return calculateLines(stack, config).also {
            cachedLines = it
            cachedStack = stack?.copy()
            cachedConfigFlags = configFlags
            cachedBlacklist = blacklist
            cachedTranslationTime = translationTime
        }
    }

    private fun configFlags(config: BrainageHudParity.EnchantInfoHud): Int =
        (if (config.showItemName) 1 else 0) or
            (if (config.showEnchantments) 2 else 0) or
            (if (config.showMaxLevels) 4 else 0) or
            (if (config.showMissingEnchantments) 8 else 0) or
            (if (config.showMissingHeader) 16 else 0)

    private fun calculateLines(stack: ItemStack?, config: BrainageHudParity.EnchantInfoHud): List<String> {
        if (stack == null) return emptyList()

        val present = EnchantmentUtils.present(stack)
        // an item that is neither enchantable nor enchanted has nothing to report
        if (present.isEmpty() && EnchantmentUtils.all.none { it.canApply(stack) }) return emptyList()

        val lines = mutableListOf<String>()

        if (config.showItemName) lines.add("${ChatUtils.boldChar}${stack.displayName}")

        if (config.showEnchantments) {
            for ((enchantment, level) in present) {
                val maxLevel = enchantment.maxLevel
                // an enchantment shown without a maximum level is already at that maximum
                val suffix = if (config.showMaxLevels && level < maxLevel) " (max $maxLevel)" else ""
                lines.add(EnchantmentUtils.fullName(enchantment, level) + suffix)
            }
        }

        if (config.showMissingEnchantments) {
            val missing = EnchantmentUtils.missing(stack, present.keys)

            if (missing.isNotEmpty() && config.showMissingHeader) lines.add("${ChatUtils.boldChar}Missing:")

            for (group in EnchantmentUtils.groupConflicting(missing)) {
                lines.add(group.joinToString(" / ") { EnchantmentUtils.fullName(it, it.maxLevel) })
            }
        }

        return lines
    }
}
