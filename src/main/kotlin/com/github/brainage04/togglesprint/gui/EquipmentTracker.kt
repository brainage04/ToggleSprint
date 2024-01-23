package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object EquipmentTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun equipmentTracker() {
        if (!guiElements.equipmentTracker.coreSettings.isEnabled) return
        val thePlayer = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf(
            ""
        )

        val hand = thePlayer.getCurrentEquippedItem() ?: return
        val head = thePlayer.getCurrentArmor(0) ?: return
        val chest = thePlayer.getCurrentArmor(1) ?: return
        val legs = thePlayer.getCurrentArmor(2) ?: return
        val feet = thePlayer.getCurrentArmor(3) ?: return

        RenderGuiData.renderElement(
            guiElements.equipmentTracker.coreSettings.x,
            guiElements.equipmentTracker.coreSettings.y,
            guiElements.equipmentTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}