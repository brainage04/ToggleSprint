package io.github.brainage04.togglesprint.hud.inventory_trackers

import io.github.brainage04.togglesprint.config.categories.GUIElements
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.util.ChatUtils
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack

object EquipmentTracker {
    fun render(coreSettings: GUIElements.CoreSettings) {
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf<String>()
        val iconStacks = arrayListOf<ItemStack?>()

        val equipmentList = arrayListOf(
            thePlayer.currentEquippedItem, // Hand
            thePlayer.getCurrentArmor(3), // Helmet
            thePlayer.getCurrentArmor(2), // Chestplate
            thePlayer.getCurrentArmor(1), // Leggings
            thePlayer.getCurrentArmor(0) // Boots
        )

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

                val durabilityPercentage = (equipmentList[i].maxDamage - equipmentList[i].itemDamage).toDouble() / equipmentList[i].maxDamage * 100.0

                currentLine += when {
                    durabilityPercentage <= 20.0 -> ChatUtils.redChar
                    durabilityPercentage <= 50.0 -> ChatUtils.yellowChar
                    else -> ChatUtils.greenChar
                }

                when (ConfigUtils.inventoryTrackers.equipmentTracker.durabilityFormat) {
                    0 -> currentLine += "${roundDecimalPlaces(durabilityPercentage, ConfigUtils.inventoryTrackers.equipmentTracker.durabilityDecimalPlaces)}%"
                    1 -> currentLine += "${equipmentList[i].maxDamage - equipmentList[i].itemDamage} / ${equipmentList[i].maxDamage}"
                    2 -> currentLine += equipmentList[i].maxDamage - equipmentList[i].itemDamage
                }
            }

            textArray.add(currentLine)
            iconStacks.add(if (ConfigUtils.inventoryTrackers.equipmentTracker.prefixFormat == 0) equipmentList[i] else null)
        }

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
            val stack = iconStacks.getOrNull(line.sourceIndex) ?: continue
            val iconY = line.y + (font.FONT_HEIGHT - ICON_SIZE) / 2
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            minecraft.renderItem.renderItemAndEffectIntoGUI(stack, line.x, iconY)
            if (ConfigUtils.inventoryTrackers.equipmentTracker.showDurabilityBar) {
                minecraft.renderItem.renderItemOverlayIntoGUI(font, stack, line.x, iconY, null)
            }
            GlStateManager.popMatrix()
        }
    }

    private const val ICON_SIZE = 16
}
