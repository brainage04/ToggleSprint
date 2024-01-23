package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft
import net.minecraft.entity.EnumCreatureType

object EntityTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun entityTracker() {
        if (!guiElements.entityTracker.coreSettings.isEnabled) return

        val theWorld = Minecraft.getMinecraft().theWorld ?: return

        val entities = theWorld.loadedEntityList

        val textArray = arrayListOf(
            "§f§b${entities.size} entities loaded"
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

        var allEntitiesDescriptor = "§f§b$allCategoriesCount grouped"
        if (guiElements.entityTracker.showOthers) allEntitiesDescriptor += ", ${entities.size - allCategoriesCount} non-grouped"

        textArray.add(allEntitiesDescriptor)

        if (guiElements.entityTracker.showCreatures
            || guiElements.entityTracker.showWaterCreatures
            || guiElements.entityTracker.showAmbients
            || guiElements.entityTracker.showMonsters) {
            textArray.add("§f§bEntity groups: ")
        }
        if (guiElements.entityTracker.showCreatures) textArray.add("§f  - Creatures: $creaturesCount")
        if (guiElements.entityTracker.showWaterCreatures) textArray.add("§f  - Water Creatures: $waterCreaturesCount")
        if (guiElements.entityTracker.showAmbients) textArray.add("§f  - Ambients: $ambientsCount")
        if (guiElements.entityTracker.showMonsters) textArray.add("§f  - Monsters: $monstersCount")

        RenderGuiData.renderElement(
            guiElements.entityTracker.coreSettings.x,
            guiElements.entityTracker.coreSettings.y,
            guiElements.entityTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}