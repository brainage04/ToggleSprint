package io.github.brainage04.togglesprint.hud.inventory_trackers

import io.github.brainage04.togglesprint.hud.inventory_trackers.core.InventoryTrackerShared
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.Item

/** How many of each throwable or shootable item the player carries. Kinds they have none of are left out. */
object ProjectileTracker {
    fun lines(): List<String> {
        val player = Minecraft.getMinecraft().thePlayer ?: return emptyList()
        val config = ConfigUtils.inventoryTrackers.projectileTracker
        val slots = player.inventory.mainInventory
        val rows = ArrayList<String>(4)

        fun add(shown: Boolean, label: String, item: Item) {
            if (!shown) return
            val count = InventoryTrackerShared.count(slots) { it.item == item }
            if (count.total > 0) rows.add(InventoryTrackerShared.format(label, count, config.showSlotCounts))
        }

        add(config.showArrows, "Arrows", Items.arrow)
        add(config.showSnowballs, "Snowballs", Items.snowball)
        add(config.showEggs, "Eggs", Items.egg)
        add(config.showEnderPearls, "Ender Pearls", Items.ender_pearl)

        return InventoryTrackerShared.withHeader("Projectiles", rows)
    }
}
