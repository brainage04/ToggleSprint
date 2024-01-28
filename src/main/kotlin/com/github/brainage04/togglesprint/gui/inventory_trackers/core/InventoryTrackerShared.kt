package com.github.brainage04.togglesprint.gui.inventory_trackers.core

import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.item.Item

object InventoryTrackerShared {
    data class InventoryTrackerItem(
        val name: String,
        val item: Item,
        var count: Int = 0,
        var countArray: ArrayList<Int> = arrayListOf()
    )

    val primaryChars = ChatUtils.colourChars[ConfigUtils.globalGuiSettings.primaryColour] + ChatUtils.effectChars[ConfigUtils.globalGuiSettings.primaryEffect]

    fun trackInventoryItems(itemList: ArrayList<InventoryTrackerItem>, includeArrays: Boolean): ArrayList<String> {
        val textArray: ArrayList<String> = arrayListOf()

        val mainInventory = Minecraft.getMinecraft().thePlayer.inventory.mainInventory ?: return textArray

        for (currentSlot in mainInventory) {
            for (item in itemList) {
                if (currentSlot == null) break // skip all iterations of THIS for loop (j) but not the outside one (i)

                if (currentSlot.item == item.item) {
                    item.count += currentSlot.stackSize
                    item.countArray.add(currentSlot.stackSize)
                }
            }
        }

        for (item in itemList) if (item.count > 0) {
            var currentLine = "${primaryChars + item.name}: ${item.count}"

            if (includeArrays && item.countArray.size > 1) currentLine += " ${item.countArray}"

            textArray.add(currentLine)
        }

        return textArray
    }
}