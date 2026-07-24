package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * Backports BrainageHUD's ambient-light override by replacing the active
 * dimension's 16-entry light table and restoring its exact original values.
 */
class FullbrightHandler {
    private var activeTable: FloatArray? = null
    private var originalTable: FloatArray? = null

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        val world = Minecraft.getMinecraft().theWorld
        if (world == null) {
            restore()
            return
        }

        val ambientLight = ConfigUtils.brainageHudParity.fullbright
        if (ambientLight == 0.0f) {
            restore()
        } else {
            apply(world.provider.lightBrightnessTable, ambientLight)
        }
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        if (event.world.isRemote) restore()
    }

    /** May be called by the mod's client shutdown lifecycle hook. Safe to call repeatedly. */
    fun onClientShutdown() {
        restore()
    }

    private fun apply(table: FloatArray, ambientLight: Float) {
        if (activeTable !== table) {
            restore()
            activeTable = table
            originalTable = table.clone()
        }
        fillLightTable(table, ambientLight)
    }

    private fun restore() {
        val table = activeTable
        val original = originalTable
        if (table != null && original != null) {
            System.arraycopy(original, 0, table, 0, Math.min(table.size, original.size))
        }
        activeTable = null
        originalTable = null
    }

    companion object {
        /** The vanilla light-table formula with DimensionType.ambientLight substituted. */
        fun fillLightTable(table: FloatArray, ambientLight: Float) {
            require(table.size >= 16) { "A Minecraft light table must contain 16 entries" }
            for (lightLevel in 0..15) {
                val darkness = 1.0f - lightLevel.toFloat() / 15.0f
                table[lightLevel] = (1.0f - darkness) / (darkness * 3.0f + 1.0f) * (1.0f - ambientLight) + ambientLight
            }
        }
    }
}
