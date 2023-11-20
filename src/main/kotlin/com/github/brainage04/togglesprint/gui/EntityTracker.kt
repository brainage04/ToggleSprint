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

        val textArray = arrayListOf(
            "§f§b${theWorld.loadedEntityList.size} entities loaded"
        )

        val players = theWorld.playerEntities
        val entities = theWorld.loadedEntityList

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

        val allCategoriesCount = players.size + creaturesCount + waterCreaturesCount + ambientsCount + monstersCount

        if (guiElements.entityTracker.showPlayers) textArray.add("§fPlayers: ${players.size}")
        if (guiElements.entityTracker.showPlayerNames) for (player in players) textArray.add("§f${player.name}")
        if (guiElements.entityTracker.showCreatures) textArray.add("§fCreatures: $creaturesCount")
        if (guiElements.entityTracker.showWaterCreatures) textArray.add("§fWater Creatures: $waterCreaturesCount")
        if (guiElements.entityTracker.showAmbients) textArray.add("§fAmbients: $ambientsCount")
        if (guiElements.entityTracker.showMonsters) textArray.add("§fMonsters: $monstersCount")
        if (guiElements.entityTracker.showOthers) textArray.add("§f§b${entities.size - allCategoriesCount} other entities loaded")

        RenderGuiData.renderElement(
            guiElements.entityTracker.coreSettings.x,
            guiElements.entityTracker.coreSettings.y,
            guiElements.entityTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}