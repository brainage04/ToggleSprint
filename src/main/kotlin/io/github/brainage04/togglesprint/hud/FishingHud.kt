package io.github.brainage04.togglesprint.hud

import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.util.BlockPos

object FishingHud {
    private const val NOT_IN_WATER = "Bobber: not in water"

    /** The active cast's catch chances from the held rod's enchantments; nothing until a bobber is out. */
    fun lines(): List<String> {
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return emptyList()
        val fishHook = player.fishEntity ?: return emptyList()
        val world = minecraft.theWorld ?: return emptyList()
        val lines = arrayListOf<String>()

        val hookPosition = BlockPos(fishHook.posX, fishHook.posY, fishHook.posZ)
        if (world.getBlockState(hookPosition).block.material != Material.water) {
            lines.add(NOT_IN_WATER)
        }
        lines.add(
            FishingChances.line(
                EnchantmentHelper.getLuckOfSeaModifier(player),
                EnchantmentHelper.getLureModifier(player),
            ),
        )

        return lines
    }
}
