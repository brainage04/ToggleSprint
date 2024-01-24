package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.gui.inventory_trackers.InventoryTrackerShared.InventoryTrackerItem
import com.github.brainage04.togglesprint.gui.inventory_trackers.InventoryTrackerShared.trackInventoryItems
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.GUIUtils
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object FoodTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun foodTracker() {
        if (!guiElements.foodTracker.coreSettings.isEnabled) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        val itemList: ArrayList<InventoryTrackerItem> = arrayListOf(
            InventoryTrackerItem(Items.apple.getItemStackDisplayName(ItemStack(Items.apple)), Items.apple),
            InventoryTrackerItem(Items.baked_potato.getItemStackDisplayName(ItemStack(Items.baked_potato)), Items.baked_potato),
            InventoryTrackerItem(Items.bread.getItemStackDisplayName(ItemStack(Items.bread)), Items.bread),
            InventoryTrackerItem(Items.carrot.getItemStackDisplayName(ItemStack(Items.carrot)), Items.carrot),
            InventoryTrackerItem(Items.cooked_beef.getItemStackDisplayName(ItemStack(Items.cooked_beef)), Items.cooked_beef),
            InventoryTrackerItem(Items.cooked_fish.getItemStackDisplayName(ItemStack(Items.cooked_fish)), Items.cooked_fish),
            InventoryTrackerItem(Items.cooked_mutton.getItemStackDisplayName(ItemStack(Items.cooked_mutton)), Items.cooked_mutton),
            InventoryTrackerItem(Items.cooked_rabbit.getItemStackDisplayName(ItemStack(Items.cooked_rabbit)), Items.cooked_rabbit),
            InventoryTrackerItem(Items.cooked_chicken.getItemStackDisplayName(ItemStack(Items.cooked_chicken)), Items.cooked_chicken),
            InventoryTrackerItem(Items.cooked_porkchop.getItemStackDisplayName(ItemStack(Items.cooked_porkchop)), Items.cooked_porkchop),
            InventoryTrackerItem(Items.cookie.getItemStackDisplayName(ItemStack(Items.cookie)), Items.cookie),
            InventoryTrackerItem(Items.golden_apple.getItemStackDisplayName(ItemStack(Items.golden_apple)), Items.golden_apple),
            InventoryTrackerItem(Items.golden_carrot.getItemStackDisplayName(ItemStack(Items.golden_carrot)), Items.golden_carrot),
            InventoryTrackerItem(Items.melon.getItemStackDisplayName(ItemStack(Items.melon)), Items.melon),
            InventoryTrackerItem(Items.potato.getItemStackDisplayName(ItemStack(Items.potato)), Items.potato),
            InventoryTrackerItem(Items.pumpkin_pie.getItemStackDisplayName(ItemStack(Items.pumpkin_pie)), Items.pumpkin_pie),
            InventoryTrackerItem(Items.rabbit_stew.getItemStackDisplayName(ItemStack(Items.rabbit_stew)), Items.rabbit_stew)
        )

        val textArray = arrayListOf(
            "${GUIUtils.secondaryChars}Food:"
        )

        textArray.addAll(trackInventoryItems(itemList))

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            guiElements.foodTracker.coreSettings.x,
            guiElements.foodTracker.coreSettings.y,
            guiElements.foodTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}