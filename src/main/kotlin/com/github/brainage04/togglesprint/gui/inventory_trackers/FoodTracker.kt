package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.gui.inventory_trackers.core.InventoryTrackerShared
import com.github.brainage04.togglesprint.gui.inventory_trackers.core.InventoryTrackerShared.trackInventoryItems
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Items

object FoodTracker {
    fun foodTracker() {
        if (!ConfigUtils.inventoryTrackers.foodTracker.coreSettings.isEnabled) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        val itemList: ArrayList<InventoryTrackerShared.InventoryTrackerItem> = arrayListOf(
            InventoryTrackerShared.InventoryTrackerItem("Apple", Items.apple),
            InventoryTrackerShared.InventoryTrackerItem("Baked Potato", Items.baked_potato),
            InventoryTrackerShared.InventoryTrackerItem("Bread", Items.bread),
            InventoryTrackerShared.InventoryTrackerItem("Carrot", Items.carrot),
            InventoryTrackerShared.InventoryTrackerItem("Cake", Items.cake),
            InventoryTrackerShared.InventoryTrackerItem("Cooked Fish", Items.cooked_fish),
            InventoryTrackerShared.InventoryTrackerItem("Cooked Mutton", Items.cooked_mutton),
            InventoryTrackerShared.InventoryTrackerItem("Cooked Rabbit", Items.cooked_rabbit),
            InventoryTrackerShared.InventoryTrackerItem("Cooked Chicken", Items.cooked_chicken),
            InventoryTrackerShared.InventoryTrackerItem("Cooked Porkchop", Items.cooked_porkchop),
            InventoryTrackerShared.InventoryTrackerItem("Cookie", Items.cookie),
            InventoryTrackerShared.InventoryTrackerItem("Fish", Items.fish),
            InventoryTrackerShared.InventoryTrackerItem("Golden Apple", Items.golden_apple),
            InventoryTrackerShared.InventoryTrackerItem("Golden Carrot", Items.golden_carrot),
            InventoryTrackerShared.InventoryTrackerItem("Melon", Items.melon),
            InventoryTrackerShared.InventoryTrackerItem("Mushroom Stew", Items.mushroom_stew),
            InventoryTrackerShared.InventoryTrackerItem("Potato", Items.potato),
            InventoryTrackerShared.InventoryTrackerItem("Pumpkin Pie", Items.pumpkin_pie),
            InventoryTrackerShared.InventoryTrackerItem("Rabbit Stew", Items.rabbit_stew),
            InventoryTrackerShared.InventoryTrackerItem("Raw Beef", Items.beef),
            InventoryTrackerShared.InventoryTrackerItem("Raw Chicken", Items.chicken),
            InventoryTrackerShared.InventoryTrackerItem("Raw Mutton", Items.mutton),
            InventoryTrackerShared.InventoryTrackerItem("Raw Porkchop", Items.porkchop),
            InventoryTrackerShared.InventoryTrackerItem("Raw Rabbit", Items.rabbit),
            InventoryTrackerShared.InventoryTrackerItem("Steak", Items.cooked_beef),
        )

        val textArray = arrayListOf(
            "${ConfigUtils.secondaryChars}Food:"
        )

        val tempItemList: ArrayList<InventoryTrackerShared.InventoryTrackerItem> = arrayListOf()

        for (number in ConfigUtils.inventoryTrackers.foodTracker.itemTypes) {
            tempItemList.add(itemList[number])
        }

        textArray.addAll(trackInventoryItems(tempItemList, ConfigUtils.inventoryTrackers.foodTracker.includeArrays))

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            ConfigUtils.inventoryTrackers.foodTracker.coreSettings.x,
            ConfigUtils.inventoryTrackers.foodTracker.coreSettings.y,
            ConfigUtils.inventoryTrackers.foodTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}