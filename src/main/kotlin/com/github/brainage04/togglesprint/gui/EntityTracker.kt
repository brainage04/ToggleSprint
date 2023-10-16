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
        if (guiElements.entityTracker.showPlayers) {
            textArray.add("§f${players.size} Players:")
            for (player in players) {
                textArray.add("§f${player.name}")
            }
        }

        val entities = theWorld.loadedEntityList

        if (guiElements.entityTracker.showCreatures) {
            var creaturesCount = 0

            for (entity in entities) {
                if (entity.isCreatureType(EnumCreatureType.CREATURE, false)) {
                    creaturesCount++
                }
            }

            textArray.add("§fCreatures: $creaturesCount")
        }

        if (guiElements.entityTracker.showWaterCreatures) {
            var waterCreaturesCount = 0

            for (entity in entities) {
                if (entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false)) {
                    waterCreaturesCount++
                }
            }

            textArray.add("§fWaterCreatures: $waterCreaturesCount")
        }

        if (guiElements.entityTracker.showAmbients) {
            var ambientsCount = 0

            for (entity in entities) {
                if (entity.isCreatureType(EnumCreatureType.AMBIENT, false)) {
                    ambientsCount++
                }
            }

            textArray.add("§fAmbients: $ambientsCount")
        }

        if (guiElements.entityTracker.showMonsters) {
            var monstersCount = 0

            for (entity in entities) {
                if (entity.isCreatureType(EnumCreatureType.MONSTER, false)) {
                    monstersCount++
                }
            }

            textArray.add("§fMonsters: $monstersCount")
        }

        RenderGuiData.renderElement(
            guiElements.entityTracker.coreSettings.x,
            guiElements.entityTracker.coreSettings.y,
            guiElements.entityTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}