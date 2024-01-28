package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object EquipmentTracker {
    fun equipmentTracker() {
        if (!ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf(
            "${ConfigUtils.secondaryChars}Equipment:"
        )

        val equipmentList = arrayListOf(
            thePlayer.getCurrentEquippedItem(), // Hand
            thePlayer.getCurrentArmor(3), // Head
            thePlayer.getCurrentArmor(2), // Chest
            thePlayer.getCurrentArmor(1), // Legs
            thePlayer.getCurrentArmor(0) // Feet
        )

        for (i in equipmentList.indices) {
            var currentLine = ConfigUtils.primaryChars

            if (equipmentList[i] == null) continue

            when (ConfigUtils.inventoryTrackers.equipmentTracker.prefixFormat) {
                0 -> { // icon (0)
                    when (ConfigUtils.inventoryTrackers.equipmentTracker.displayDurabilityBar) {
                        true -> {

                        }

                        false -> {

                        }
                    }
                }

                else -> { // name (1)
                    currentLine += if (equipmentList[i].hasDisplayName()) equipmentList[i].displayName.trim()
                    else equipmentList[i].item.getItemStackDisplayName(equipmentList[i]).trim()
                }
            }

            if (equipmentList[i].isItemStackDamageable) { // if item has durability:
                currentLine += ": " // separator between icon/name and durability

                val durabilityPercentage = ((equipmentList[i].maxDamage - equipmentList[i].itemDamage).toFloat() / equipmentList[i].maxDamage.toFloat() * 100.0f).round(
                    ConfigUtils.inventoryTrackers.equipmentTracker.decimals)

                currentLine += when {
                    durabilityPercentage <= 20f -> ChatUtils.redChar
                    durabilityPercentage <= 50f -> ChatUtils.yellowChar
                    else -> ChatUtils.greenChar
                }

                when (ConfigUtils.inventoryTrackers.equipmentTracker.durabilityFormat) {
                    0 -> currentLine += "${durabilityPercentage}%"
                    1 -> currentLine += "${equipmentList[i].maxDamage - equipmentList[i].itemDamage} / ${equipmentList[i].maxDamage}"
                    2 -> currentLine += equipmentList[i].maxDamage - equipmentList[i].itemDamage
                }
            }

            textArray.add(currentLine)
        }

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings.x,
            ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings.y,
            ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}