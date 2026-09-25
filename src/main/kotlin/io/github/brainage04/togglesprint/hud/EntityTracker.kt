package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EnumCreatureType

/** How many entities the client has loaded, grouped by kind of mob. */
object EntityTracker {
    private enum class Group(val label: String) {
        CREATURES("Creatures"),
        WATER_CREATURES("Water Creatures"),
        AMBIENT("Ambient"),
        MONSTERS("Monsters"),
        OTHERS("Others");

        companion object {
            fun of(entity: Entity): Group = when {
                entity.isCreatureType(EnumCreatureType.CREATURE, false) -> CREATURES
                entity.isCreatureType(EnumCreatureType.WATER_CREATURE, false) -> WATER_CREATURES
                entity.isCreatureType(EnumCreatureType.AMBIENT, false) -> AMBIENT
                entity.isCreatureType(EnumCreatureType.MONSTER, false) -> MONSTERS
                else -> OTHERS
            }
        }
    }

    fun lines(): List<String> {
        val world = Minecraft.getMinecraft().theWorld ?: return emptyList()
        val config = ConfigUtils.guiElements.entityTracker

        val counts = IntArray(Group.values().size)
        for (entity in world.loadedEntityList) counts[Group.of(entity).ordinal]++

        val lines = arrayListOf("Entities: ${world.loadedEntityList.size}")
        if (config.showCreatures) lines.add(group(Group.CREATURES, counts))
        if (config.showWaterCreatures) lines.add(group(Group.WATER_CREATURES, counts))
        if (config.showAmbient) lines.add(group(Group.AMBIENT, counts))
        if (config.showMonsters) lines.add(group(Group.MONSTERS, counts))
        if (config.showOthers) lines.add(group(Group.OTHERS, counts))
        return lines
    }

    private fun group(group: Group, counts: IntArray) = "  ${group.label}: ${counts[group.ordinal]}"
}
