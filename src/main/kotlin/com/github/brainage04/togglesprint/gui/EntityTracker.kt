package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.EnumCreatureType

object EntityTracker {
    fun lines(): List<String> {
        val theWorld = Minecraft.getMinecraft().theWorld ?: return emptyList()
        val config = ConfigUtils.guiElements.entityTracker

        val entities = theWorld.loadedEntityList

        val textArray = arrayListOf(
            "${entities.size} entities loaded"
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

        var allEntitiesDescriptor = "$allCategoriesCount grouped"
        if (config.showOthers) allEntitiesDescriptor += ", ${entities.size - allCategoriesCount} non-grouped"

        textArray.add(allEntitiesDescriptor)

        if (config.showCreatures || config.showWaterCreatures || config.showAmbients || config.showMonsters) {
            textArray.add("${ChatUtils.boldChar}Entity groups:")
        }
        if (config.showCreatures) textArray.add("  - Creatures: $creaturesCount")
        if (config.showWaterCreatures) textArray.add("  - Water Creatures: $waterCreaturesCount")
        if (config.showAmbients) textArray.add("  - Ambients: $ambientsCount")
        if (config.showMonsters) textArray.add("  - Monsters: $monstersCount")

        return textArray
    }
}
