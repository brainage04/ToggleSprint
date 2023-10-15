package com.github.brainage04.togglesprint.gui.core

import com.github.brainage04.togglesprint.gui.PlayerMotionTracker.Companion.playerMotionTracker
import com.github.brainage04.togglesprint.gui.PlayerPositionTracker.Companion.playerPositionTracker
import com.github.brainage04.togglesprint.gui.PlayerRotationTracker.Companion.playerRotationTracker
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.Companion.toggleSprintTracker
import io.github.moulberry.moulconfig.observer.Property
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class RenderGuiData {
    enum class DisplayAnchor {
        TOPLEFT,
        TOPRIGHT,
        BOTTOMLEFT,
        BOTTOMRIGHT,
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        GlStateManager.pushMatrix()

        playerPositionTracker()
        playerMotionTracker()
        playerRotationTracker()
        toggleSprintTracker()

        GlStateManager.popMatrix()
    }

    companion object {
        private var paddingInPixels = 2

        fun renderElement(x: Double, y: Double, displayAnchor: Property<DisplayAnchor>, text: String, lineWidth: Int = -1, lineHeight: Int = -1) {
            val minecraft = Minecraft.getMinecraft() ?: return
            val renderer = minecraft.renderManager.fontRenderer ?: return

            val widthInPixels = if (lineWidth == -1) renderer.getStringWidth(text) else 0
            val heightInPixels = if (lineHeight == -1) renderer.FONT_HEIGHT else 0

            val posX = when (displayAnchor) {
                Property.of(DisplayAnchor.TOPRIGHT), Property.of(DisplayAnchor.BOTTOMRIGHT) -> minecraft.displayWidth - x - widthInPixels
                else -> x
            }
            val posY = when (displayAnchor) {
                Property.of(DisplayAnchor.BOTTOMLEFT), Property.of(DisplayAnchor.BOTTOMRIGHT) -> minecraft.displayHeight - y - heightInPixels
                else -> y
            }

            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            GlStateManager.translate(posX, posY, 0.0)
            renderer.drawStringWithShadow(text, 0f, 0f, 0)
            GlStateManager.popMatrix()
        }

        fun renderElement(x: Double, y: Double, displayAnchor: Property<DisplayAnchor>, textArray: Array<String>) {
            val minecraft = Minecraft.getMinecraft() ?: return
            val renderer = minecraft.renderManager.fontRenderer ?: return

            for (i in textArray.indices) {
                val widthInPixels = renderer.getStringWidth(textArray[i])
                val heightInPixels = renderer.FONT_HEIGHT

                renderElement(x, y + ((heightInPixels + paddingInPixels) * i), displayAnchor, textArray[i], widthInPixels, heightInPixels)
            }
        }
    }
}