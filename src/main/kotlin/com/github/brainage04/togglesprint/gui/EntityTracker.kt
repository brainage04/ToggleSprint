package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.GUIUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.EnumCreatureType

object EntityTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun entityTracker() {
        if (!guiElements.entityTracker.coreSettings.isEnabled) return

        val theWorld = Minecraft.getMinecraft().theWorld ?: return

        val entities = theWorld.loadedEntityList

        val textArray = arrayListOf(
            "${GUIUtils.secondaryChars + entities.size} entities loaded"
        )

        var creaturesCount = 0
        var waterCreaturesCount = 0
        var ambientsCount = 0
        var monstersCount = 0

        for (entity in entities) {
            if (entity.isCreatureType(EnumCreatureType.CREATURE, false)) creaturesCount++
            if (entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false)) waterCreaturesCount++
            if (entity.isCreatureType(EnumCreatureType.AMBIENT, false)) ambientsCount++
            if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) monstersCount++
        }

        val allCategoriesCount = creaturesCount + waterCreaturesCount + ambientsCount + monstersCount

        var allEntitiesDescriptor = "${GUIUtils.secondaryChars + allCategoriesCount} grouped"
        if (guiElements.entityTracker.showOthers) allEntitiesDescriptor += ", ${entities.size - allCategoriesCount} non-grouped"

        textArray.add(allEntitiesDescriptor)

        if (guiElements.entityTracker.showCreatures
            || guiElements.entityTracker.showWaterCreatures
            || guiElements.entityTracker.showAmbients
            || guiElements.entityTracker.showMonsters) {
            textArray.add("${GUIUtils.secondaryChars}Entity groups: ")
        }
        if (guiElements.entityTracker.showCreatures) textArray.add("${GUIUtils.primaryChars}  - Creatures: $creaturesCount")
        if (guiElements.entityTracker.showWaterCreatures) textArray.add("${GUIUtils.primaryChars}  - Water Creatures: $waterCreaturesCount")
        if (guiElements.entityTracker.showAmbients) textArray.add("${GUIUtils.primaryChars}  - Ambients: $ambientsCount")
        if (guiElements.entityTracker.showMonsters) textArray.add("${GUIUtils.primaryChars}  - Monsters: $monstersCount")

        RenderGuiData.renderElement(
            guiElements.entityTracker.coreSettings.x,
            guiElements.entityTracker.coreSettings.y,
            guiElements.entityTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}