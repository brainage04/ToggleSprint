package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.gui.inventory_trackers.core.InventoryTrackerShared
import com.github.brainage04.togglesprint.gui.inventory_trackers.core.InventoryTrackerShared.trackInventoryItems
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object ProjectileTracker {
    fun projectileTracker() {
        if (!ConfigUtils.inventoryTrackers.projectileTracker.coreSettings.isEnabled) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        val itemList: ArrayList<InventoryTrackerShared.InventoryTrackerItem> = arrayListOf(
            InventoryTrackerShared.InventoryTrackerItem(
                Items.arrow.getItemStackDisplayName(ItemStack(Items.arrow)),
                Items.arrow
            ),
            InventoryTrackerShared.InventoryTrackerItem(
                Items.snowball.getItemStackDisplayName(ItemStack(Items.snowball)),
                Items.snowball
            ),
            InventoryTrackerShared.InventoryTrackerItem(
                Items.egg.getItemStackDisplayName(ItemStack(Items.egg)),
                Items.egg
            ),
            InventoryTrackerShared.InventoryTrackerItem(
                Items.ender_pearl.getItemStackDisplayName(ItemStack(Items.ender_pearl)),
                Items.ender_pearl
            ),
        )

        val textArray = arrayListOf(
            "${ConfigUtils.secondaryChars}Projectiles:"
        )

        val tempItemList: ArrayList<InventoryTrackerShared.InventoryTrackerItem> = arrayListOf()

        for (number in ConfigUtils.inventoryTrackers.projectileTracker.itemTypes) {
            tempItemList.add(itemList[number])
        }

        textArray.addAll(trackInventoryItems(tempItemList, ConfigUtils.inventoryTrackers.projectileTracker.includeArrays))

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            ConfigUtils.inventoryTrackers.projectileTracker.coreSettings.x,
            ConfigUtils.inventoryTrackers.projectileTracker.coreSettings.y,
            ConfigUtils.inventoryTrackers.projectileTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}