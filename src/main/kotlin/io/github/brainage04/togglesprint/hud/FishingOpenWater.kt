package io.github.brainage04.togglesprint.hud

/**
 * The block volume required for a cast to be treated as open water.
 *
 * Every 5x5 layer must contain only source water or only air/lily pads. The
 * bottom layer must be water, and no water layer may occur above an
 * air/lily-pad layer. Keeping the lookup independent of Minecraft makes this
 * rule deterministic and testable without a client world.
 */
object FishingOpenWater {
    enum class BlockType {
        WATER,
        ABOVE_WATER,
        OTHER,
    }

    interface BlockLookup {
        fun blockAt(x: Int, y: Int, z: Int): BlockType
    }

    /**
     * Backports the modern four-layer open-water check: each 5x5 layer must be
     * homogeneous source water or air/lily pad, the bottom layer must be water,
     * and water cannot occur above an air layer.
     */
    fun isOpenWater(hookX: Int, hookY: Int, hookZ: Int, blocks: BlockLookup): Boolean {
        var previousLayer = BlockType.OTHER
        for (yOffset in -1..2) {
            var layerType: BlockType? = null
            for (xOffset in -2..2) {
                for (zOffset in -2..2) {
                    val current = blocks.blockAt(hookX + xOffset, hookY + yOffset, hookZ + zOffset)
                    if (layerType == null) {
                        layerType = current
                    } else if (current != layerType) {
                        return false
                    }
                }
            }

            when (layerType) {
                BlockType.OTHER, null -> return false
                BlockType.ABOVE_WATER -> if (previousLayer == BlockType.OTHER) return false
                BlockType.WATER -> if (previousLayer == BlockType.ABOVE_WATER) return false
            }
            previousLayer = layerType
        }
        return true
    }
}
