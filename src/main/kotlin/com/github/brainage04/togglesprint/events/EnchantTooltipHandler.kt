package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.EnchantmentUtils
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/** Highlights enchantments at their maximum level in bold in item tooltips. */
class EnchantTooltipHandler {
    @SubscribeEvent
    fun onItemTooltip(event: ItemTooltipEvent) {
        if (!ConfigUtils.brainageHudParity.enchantInfo.highlightMaxLevelEnchants) return
        val stack = event.itemStack ?: return

        // covers enchanted books too, whose enchantments are stored separately
        val maxedEnchantmentLines = EnchantmentUtils.present(stack)
            .filter { (enchantment, level) -> level == enchantment.maxLevel }
            .mapTo(HashSet()) { (enchantment, level) -> enchantment.getTranslatedName(level) }
        if (maxedEnchantmentLines.isEmpty()) return

        // vanilla renders each enchantment as a line of exactly its translated name, so exact
        // matching leaves lore and other mods' lines that merely start with an enchantment name
        // untouched; the first line is the item's (possibly renamed) name
        val lines = event.toolTip
        for (i in 1 until lines.size) {
            val line = lines[i]
            if (EnumChatFormatting.getTextWithoutFormattingCodes(line) in maxedEnchantmentLines) lines[i] = bold(line)
        }
    }

    /** Inserts the bold code after any leading formatting codes, since a colour code resets bold. */
    private fun bold(line: String): String {
        var textStart = 0
        while (textStart + 1 < line.length && line[textStart] == '§') textStart += 2
        return line.substring(0, textStart) + ChatUtils.boldChar + line.substring(textStart)
    }
}
