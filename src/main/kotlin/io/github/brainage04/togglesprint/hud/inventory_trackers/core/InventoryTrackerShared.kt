package io.github.brainage04.togglesprint.hud.inventory_trackers.core

import io.github.brainage04.togglesprint.util.ChatUtils
import net.minecraft.item.ItemStack

/** Counts items across the player's inventory for the inventory tracker HUDs. */
object InventoryTrackerShared {
    /** How many matching items there are in total, and in each slot that holds some. */
    class Count {
        var total = 0
            private set
        val perSlot = ArrayList<Int>()

        fun add(amount: Int) {
            total += amount
            perSlot.add(amount)
        }
    }

    /** Counts the items in [slots] (the hotbar from left to right, then the inventory rows) that [matches] accepts. */
    fun count(slots: Array<ItemStack?>, matches: (ItemStack) -> Boolean): Count {
        val count = Count()
        for (stack in slots) {
            if (stack != null && matches(stack)) count.add(stack.stackSize)
        }
        return count
    }

    /** Counts every matching item grouped by [key], in the order each group is first found in the slots. */
    fun <K> countBy(slots: Array<ItemStack?>, matches: (ItemStack) -> Boolean, key: (ItemStack) -> K): Map<K, Count> {
        val counts = LinkedHashMap<K, Count>()
        for (stack in slots) {
            if (stack != null && matches(stack)) counts.getOrPut(key(stack)) { Count() }.add(stack.stackSize)
        }
        return counts
    }

    /** "Arrows: 96", or "Arrows: 96 [64, 32]" when the items are split over several slots. */
    fun format(label: String, count: Count, showSlots: Boolean): String {
        var line = "$label: ${count.total}"
        if (showSlots && count.perSlot.size > 1) line += " ${count.perSlot}"
        return line
    }

    /** The bold [title] header followed by [rows], or the header with a red N/A when there are none. */
    fun withHeader(title: String, rows: List<String>): List<String> =
        if (rows.isEmpty()) listOf("${ChatUtils.boldChar}$title:${ChatUtils.redChar} N/A")
        else listOf("${ChatUtils.boldChar}$title:") + rows
}
