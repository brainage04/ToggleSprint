package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack

object EquipmentTracker {
    fun equipmentTracker() {
        if (!ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf("${ConfigUtils.secondaryChars}Equipment:")
        val iconStacks = arrayListOf<ItemStack?>()

        val equipmentList = arrayListOf(
            thePlayer.currentEquippedItem, // Hand
            thePlayer.getCurrentArmor(3), // Helmet
            thePlayer.getCurrentArmor(2), // Chestplate
            thePlayer.getCurrentArmor(1), // Leggings
            thePlayer.getCurrentArmor(0) // Boots
        )

        if (ConfigUtils.inventoryTrackers.equipmentTracker.itemTypes == null) { // prevents crashing when users update their mod from a previous version
            ToggleSprintMain.LOGGER.warn("equipmentTracker.itemTypes was missing, this may be due to an update. updating config...")
            ToggleSprintMain.config.inventoryTrackers.equipmentTracker.itemTypes = ArrayList(mutableListOf(0, 1, 2, 3, 4))
            ToggleSprintMain.configManager.save()
            ToggleSprintMain.LOGGER.warn("Config updated.")
        }

        for (i in ConfigUtils.inventoryTrackers.equipmentTracker.itemTypes) {
            var currentLine = ConfigUtils.primaryChars

            if (i !in equipmentList.indices || equipmentList[i] == null) continue

            when (ConfigUtils.inventoryTrackers.equipmentTracker.prefixFormat) {
                0 -> { // reserve the icon column; the item is rendered below at its HUD position
                    currentLine += "    "
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
            iconStacks.add(if (ConfigUtils.inventoryTrackers.equipmentTracker.prefixFormat == 0) equipmentList[i] else null)
        }

        if (textArray.size < 2) textArray[0] += "${ChatUtils.redChar} N/A"

        val settings = ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings
        RenderGuiData.renderElement(settings.x, settings.y, settings.anchorCorner, textArray)
        renderIcons(iconStacks, textArray, settings.x, settings.y, settings.anchorCorner)
    }

    private fun renderIcons(iconStacks: List<ItemStack?>, lines: List<String>, x: Double, y: Double, anchor: Int) {
        if (iconStacks.none { it != null }) return
        val minecraft = Minecraft.getMinecraft()
        val font = minecraft.renderManager.fontRenderer ?: return
        val resolution = ScaledResolution(minecraft)
        val lineHeight = font.FONT_HEIGHT + ConfigUtils.globalGuiSettings.paddingInPixels
        for (itemIndex in iconStacks.indices) {
            val stack = iconStacks[itemIndex] ?: continue
            val lineIndex = itemIndex + 1
            val line = lines[lineIndex]
            val iconX = when (anchor) {
                1, 3, 5 -> resolution.scaledWidth - x - font.getStringWidth(line)
                6, 7, 8 -> (resolution.scaledWidth - x - font.getStringWidth(line)) / 2
                else -> x
            }.toInt()
            val requestedY = when (anchor) {
                4, 5, 8 -> y + lineHeight * (lines.size - 1) - lineHeight * lineIndex * 2
                2, 3, 7 -> y + lineHeight * (lines.size - 1 - lineIndex)
                else -> y + lineHeight * lineIndex
            }
            val iconY = when (anchor) {
                2, 3, 7 -> resolution.scaledHeight - requestedY - 16
                4, 5, 8 -> (resolution.scaledHeight - requestedY - 16) / 2
                else -> requestedY
            }.toInt()
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            minecraft.renderItem.renderItemAndEffectIntoGUI(stack, iconX, iconY)
            if (ConfigUtils.inventoryTrackers.equipmentTracker.displayDurabilityBar) {
                minecraft.renderItem.renderItemOverlayIntoGUI(font, stack, iconX, iconY, null)
            }
            GlStateManager.popMatrix()
        }
    }
}