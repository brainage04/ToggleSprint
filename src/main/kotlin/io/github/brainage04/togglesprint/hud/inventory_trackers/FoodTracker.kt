package io.github.brainage04.togglesprint.hud.inventory_trackers

import io.github.brainage04.togglesprint.hud.inventory_trackers.core.InventoryTrackerShared
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack

/** How many of each food the player carries, in the order they appear in the inventory. */
object FoodTracker {
    fun lines(): List<String> {
        val player = Minecraft.getMinecraft().thePlayer ?: return emptyList()
        val showSlots = ConfigUtils.inventoryTrackers.foodTracker.showSlotCounts

        // items with subtypes (fish, golden apples) are told apart by their metadata
        val rows = InventoryTrackerShared.countBy(
            player.inventory.mainInventory,
            { it.item is ItemFood },
            { it.item to if (it.hasSubtypes) it.metadata else 0 },
        ).map { (key, count) ->
            InventoryTrackerShared.format(ItemStack(key.first, 1, key.second).displayName, count, showSlots)
        }

        return InventoryTrackerShared.withHeader("Food", rows)
    }
}
