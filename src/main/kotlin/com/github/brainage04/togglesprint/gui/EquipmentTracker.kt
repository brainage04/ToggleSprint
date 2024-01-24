package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.GUIUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object EquipmentTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun equipmentTracker() {
        if (!guiElements.equipmentTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf(
            "${GUIUtils.secondaryChars}Equipment:"
        )

        val equipmentNameList = arrayListOf(
            "Hand",
            "Head",
            "Chest",
            "Legs",
            "Feet"
        )

        val equipmentList = arrayListOf(
            thePlayer.getCurrentEquippedItem(), // Hand
            thePlayer.getCurrentArmor(3), // Head
            thePlayer.getCurrentArmor(2), // Chest
            thePlayer.getCurrentArmor(1), // Legs
            thePlayer.getCurrentArmor(0) // Feet
        )

        for (i in equipmentList.indices) {
            var currentLine = GUIUtils.primaryChars

            if (equipmentList[i] == null) continue

            when (guiElements.equipmentTracker.prefixFormat) {
                0 -> { // icon (0)
                    when (guiElements.equipmentTracker.displayDurabilityBar) {
                        true -> {

                        }

                        false -> {

                        }
                    }
                }

                else -> { // name (1)
                    currentLine += if (guiElements.equipmentTracker.nameFormat == 0 || !equipmentList[i].isItemStackDamageable) equipmentList[i].item.getItemStackDisplayName(equipmentList[i]) // item name (0)
                    else equipmentNameList[i] // slot name (1)
                }
            }

            if (equipmentList[i].isItemStackDamageable) { // if item has durability:
                currentLine += ": " // separator between icon/name and durability

                val durabilityPercentage = ((equipmentList[i].maxDamage - equipmentList[i].itemDamage).toFloat() / equipmentList[i].maxDamage.toFloat() * 100.0f).round(
                    guiElements.equipmentTracker.decimals)

                currentLine += when {
                    durabilityPercentage <= 20f -> ChatUtils.redChar
                    durabilityPercentage <= 50f -> ChatUtils.yellowChar
                    else -> ChatUtils.greenChar
                }

                when (guiElements.equipmentTracker.durabilityFormat) {
                    0 -> currentLine += "${durabilityPercentage}%"
                    1 -> currentLine += "${equipmentList[i].maxDamage - equipmentList[i].itemDamage} / ${equipmentList[i].maxDamage}"
                    2 -> currentLine += equipmentList[i].maxDamage - equipmentList[i].itemDamage
                }
            }

            textArray.add(currentLine)
        }

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        RenderGuiData.renderElement(
            guiElements.equipmentTracker.coreSettings.x,
            guiElements.equipmentTracker.coreSettings.y,
            guiElements.equipmentTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}