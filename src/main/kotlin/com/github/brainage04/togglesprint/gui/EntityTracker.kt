package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.EnumCreatureType

object EntityTracker {
    fun entityTracker() {
        if (!ConfigUtils.guiElements.entityTracker.coreSettings.isEnabled) return

        val theWorld = Minecraft.getMinecraft().theWorld ?: return

        val entities = theWorld.loadedEntityList

        val textArray = arrayListOf(
            "${ConfigUtils.secondaryChars + entities.size} entities loaded"
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

        var allEntitiesDescriptor = "${ConfigUtils.secondaryChars + allCategoriesCount} grouped"
        if (ConfigUtils.guiElements.entityTracker.showOthers) allEntitiesDescriptor += ", ${entities.size - allCategoriesCount} non-grouped"

        textArray.add(allEntitiesDescriptor)

        if (ConfigUtils.guiElements.entityTracker.showCreatures
            || ConfigUtils.guiElements.entityTracker.showWaterCreatures
            || ConfigUtils.guiElements.entityTracker.showAmbients
            || ConfigUtils.guiElements.entityTracker.showMonsters) {
            textArray.add("${ConfigUtils.secondaryChars}Entity groups:")
        }
        if (ConfigUtils.guiElements.entityTracker.showCreatures) textArray.add("${ConfigUtils.primaryChars}  - Creatures: $creaturesCount")
        if (ConfigUtils.guiElements.entityTracker.showWaterCreatures) textArray.add("${ConfigUtils.primaryChars}  - Water Creatures: $waterCreaturesCount")
        if (ConfigUtils.guiElements.entityTracker.showAmbients) textArray.add("${ConfigUtils.primaryChars}  - Ambients: $ambientsCount")
        if (ConfigUtils.guiElements.entityTracker.showMonsters) textArray.add("${ConfigUtils.primaryChars}  - Monsters: $monstersCount")

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.entityTracker.coreSettings.x,
            ConfigUtils.guiElements.entityTracker.coreSettings.y,
            ConfigUtils.guiElements.entityTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}