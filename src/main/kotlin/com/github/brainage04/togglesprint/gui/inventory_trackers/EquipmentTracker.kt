package com.github.brainage04.togglesprint.gui.inventory_trackers

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack

object EquipmentTracker {
    fun render(coreSettings: GUIElements.CoreSettings) {
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf("${ChatUtils.boldChar}Equipment:")
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
            var currentLine = ""

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

        val placedLines = RenderGuiData.drawElement(coreSettings, textArray)
        renderIcons(iconStacks, placedLines)
    }

    /** Draws each item's icon over the blank prefix reserved at the start of its line. */
    private fun renderIcons(iconStacks: List<ItemStack?>, placedLines: List<RenderGuiData.PlacedLine>) {
        if (iconStacks.none { it != null }) return
        val minecraft = Minecraft.getMinecraft()
        val font = minecraft.fontRendererObj ?: return
        var previousSource = -1
        for (line in placedLines) {
            // a wrapped line continues its item; only its first part carries the icon
            if (line.sourceIndex == previousSource) continue
            previousSource = line.sourceIndex
            // line 0 is the header
            val stack = iconStacks.getOrNull(line.sourceIndex - 1) ?: continue
            val iconY = line.y + (font.FONT_HEIGHT - ICON_SIZE) / 2
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            minecraft.renderItem.renderItemAndEffectIntoGUI(stack, line.x, iconY)
            if (ConfigUtils.inventoryTrackers.equipmentTracker.displayDurabilityBar) {
                minecraft.renderItem.renderItemOverlayIntoGUI(font, stack, line.x, iconY, null)
            }
            GlStateManager.popMatrix()
        }
    }

    private const val ICON_SIZE = 16
}
