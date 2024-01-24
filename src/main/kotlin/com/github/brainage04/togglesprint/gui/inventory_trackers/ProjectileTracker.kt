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

object ProjectileTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun projectileTracker() {
        if (!guiElements.projectileTracker.coreSettings.isEnabled) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        val itemList: ArrayList<InventoryTrackerItem> = arrayListOf(
            InventoryTrackerItem(Items.arrow.getItemStackDisplayName(ItemStack(Items.arrow)), Items.arrow),
            InventoryTrackerItem(Items.snowball.getItemStackDisplayName(ItemStack(Items.snowball)), Items.snowball),
            InventoryTrackerItem(Items.egg.getItemStackDisplayName(ItemStack(Items.egg)), Items.egg),
            InventoryTrackerItem(Items.ender_pearl.getItemStackDisplayName(ItemStack(Items.ender_pearl)), Items.ender_pearl),
        )

        val textArray = arrayListOf(
            "${GUIUtils.secondaryChars}Projectiles:"
        )

        textArray.addAll(trackInventoryItems(itemList, guiElements.projectileTracker.includeArrays))

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            guiElements.projectileTracker.coreSettings.x,
            guiElements.projectileTracker.coreSettings.y,
            guiElements.projectileTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}