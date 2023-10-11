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
    enum class HorizontalAlignmentType {
        LEFT,
        CENTER,
        RIGHT
    }

    enum class VerticalAlignmentType {
        TOP,
        CENTER,
        BOTTOM
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

        fun renderElement(x: Double, y: Double, horizontalAlignment: Property<HorizontalAlignmentType>, verticalAlignment: Property<VerticalAlignmentType>, text: String, lineWidth: Int = -1, lineHeight: Int = -1) {
            val minecraft = Minecraft.getMinecraft() ?: return
            val renderer = minecraft.renderManager.fontRenderer ?: return

            var widthInPixels = lineWidth
            if (widthInPixels == -1) {
                widthInPixels = renderer.getStringWidth(text)
            }
            var heightInPixels = lineHeight
            if (heightInPixels == -1) {
                heightInPixels = renderer.FONT_HEIGHT
            }

            var posX = x
            var posY = y

            when (horizontalAlignment) {
                Property.of(HorizontalAlignmentType.LEFT) -> {

                }

                Property.of(HorizontalAlignmentType.CENTER) -> {
                    posX += widthInPixels / 2
                }

                Property.of(HorizontalAlignmentType.RIGHT) -> {
                    posX += widthInPixels
                }
            }

            when (verticalAlignment) {
                Property.of(VerticalAlignmentType.TOP) -> {

                }

                Property.of(VerticalAlignmentType.CENTER) -> {
                    posY -= heightInPixels / 2
                }

                Property.of(VerticalAlignmentType.BOTTOM) -> {
                    posY -= heightInPixels
                }
            }

            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            GlStateManager.translate(posX, posY, 0.0)
            renderer.drawStringWithShadow(text, 0f, 0f, 0)
            GlStateManager.popMatrix()
        }

        fun renderElement(x: Double, y: Double, horizontalAlignment: Property<HorizontalAlignmentType>, verticalAlignment: Property<VerticalAlignmentType>, textArray: Array<String>) {
            val minecraft = Minecraft.getMinecraft() ?: return
            val renderer = minecraft.renderManager.fontRenderer ?: return

            for (i in textArray.indices) {
                val widthInPixels = renderer.getStringWidth(textArray[i])
                val heightInPixels = renderer.FONT_HEIGHT

                renderElement(x, y + ((heightInPixels + paddingInPixels) * i), horizontalAlignment, verticalAlignment, textArray[i], widthInPixels, heightInPixels)
            }
        }
    }
}