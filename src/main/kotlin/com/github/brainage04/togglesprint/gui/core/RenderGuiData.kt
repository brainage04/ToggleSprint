package com.github.brainage04.togglesprint.gui.core

import com.github.brainage04.togglesprint.gui.EntityTracker.entityTracker
import com.github.brainage04.togglesprint.gui.PingTracker.pingTracker
import com.github.brainage04.togglesprint.gui.PlayerMotionTracker.playerMotionTracker
import com.github.brainage04.togglesprint.gui.PlayerPositionTracker.playerPositionTracker
import com.github.brainage04.togglesprint.gui.PlayerRotationTracker.playerRotationTracker
import com.github.brainage04.togglesprint.gui.RealTimeTracker.realTimeTracker
import com.github.brainage04.togglesprint.gui.TPSTracker.tpsTracker
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.toggleSprintTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class RenderGuiData {
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        GlStateManager.pushMatrix()

        toggleSprintTracker()
        playerPositionTracker()
        playerMotionTracker()
        playerRotationTracker()
        entityTracker()
        realTimeTracker()
        tpsTracker()
        pingTracker()

        GlStateManager.popMatrix()
    }

    companion object {
        private var paddingInPixels = 2

        fun renderElement(x: Double, y: Double, anchorCorner: Int, text: String) {
            val minecraft = Minecraft.getMinecraft() ?: return
            val renderer = minecraft.renderManager.fontRenderer ?: return

            val scaledResolution = ScaledResolution(minecraft)
            val widthInPixels = renderer.getStringWidth(text)

            val posX = when (anchorCorner) {
                1, 3 -> scaledResolution.scaledWidth - x - widthInPixels
                else -> x
            }
            val posY = when (anchorCorner) {
                2, 3 -> scaledResolution.scaledHeight - y - renderer.FONT_HEIGHT
                else -> y
            }

            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            GlStateManager.translate(posX, posY, 0.0)
            renderer.drawStringWithShadow(text, 0f, 0f, 0)
            GlStateManager.popMatrix()
        }

        // this should be removed in favour of ArrayList version (arrays have mutable elements but immutable size, arraylists have mutable elements AND size)
        fun renderElement(x: Double, y: Double, anchorCorner: Int, textArray: Array<String>) {
            val renderer = Minecraft.getMinecraft().renderManager.fontRenderer ?: return

            val heightInPixels = (renderer.FONT_HEIGHT + paddingInPixels)

            when (anchorCorner) {
                0, 1 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[i])
                2, 3 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[textArray.indices.last - i])
            }
        }

        fun renderElement(x: Double, y: Double, anchorCorner: Int, textArray: ArrayList<String>) {
            val renderer = Minecraft.getMinecraft().renderManager.fontRenderer ?: return

            val heightInPixels = (renderer.FONT_HEIGHT + paddingInPixels)

            when (anchorCorner) {
                0, 1 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[i])
                2, 3 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[textArray.indices.last - i])
            }
        }
    }
}