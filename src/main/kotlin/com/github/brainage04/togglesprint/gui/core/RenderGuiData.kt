package com.github.brainage04.togglesprint.gui.core

import com.github.brainage04.togglesprint.gui.EntityTracker.entityTracker
import com.github.brainage04.togglesprint.gui.EquipmentTracker.equipmentTracker
import com.github.brainage04.togglesprint.gui.PingTracker.pingTracker
import com.github.brainage04.togglesprint.gui.PlayerMotionTracker.playerMotionTracker
import com.github.brainage04.togglesprint.gui.PlayerPositionTracker.playerPositionTracker
import com.github.brainage04.togglesprint.gui.PlayerRotationTracker.playerRotationTracker
import com.github.brainage04.togglesprint.gui.RealTimeTracker.realTimeTracker
import com.github.brainage04.togglesprint.gui.TPSTracker.Companion.tpsTracker
import com.github.brainage04.togglesprint.gui.ToggleSprintTracker.toggleSprintTracker
import com.github.brainage04.togglesprint.gui.inventory_trackers.FoodTracker.foodTracker
import com.github.brainage04.togglesprint.gui.inventory_trackers.ProjectileTracker.projectileTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class RenderGuiData {
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return
        //if (Minecraft.getMinecraft().thePlayer == null) return

        GlStateManager.pushMatrix()

        toggleSprintTracker()
        playerPositionTracker()
        playerMotionTracker()
        playerRotationTracker()
        equipmentTracker()
        projectileTracker()
        foodTracker()
        realTimeTracker()
        pingTracker()
        tpsTracker()
        entityTracker()

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
                1, 3, 5 -> scaledResolution.scaledWidth - x - widthInPixels // top/bottom/center right
                6, 7, 8 -> (scaledResolution.scaledWidth - x - widthInPixels) / 2 // center top/bottom, center
                else -> x
            }
            val posY = when (anchorCorner) {
                2, 3, 7 -> scaledResolution.scaledHeight - y - renderer.FONT_HEIGHT // bottom left/right/center
                4, 5, 8 -> (scaledResolution.scaledHeight - y - renderer.FONT_HEIGHT) / 2 // center left/right, center
                else -> y
            }

            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            GlStateManager.translate(posX, posY, 0.0)
            renderer.drawStringWithShadow(text, 0f, 0f, 0)
            GlStateManager.popMatrix()
        }

        fun renderElement(x: Double, y: Double, anchorCorner: Int, textArray: ArrayList<String>) {
            val renderer = Minecraft.getMinecraft().renderManager.fontRenderer ?: return

            val heightInPixels = (renderer.FONT_HEIGHT + paddingInPixels)

            when (anchorCorner) {
                0, 1, 6 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[i]) // top left/right/center
                4, 5, 8 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * (textArray.size - 1)) - (heightInPixels * i * 2), anchorCorner, textArray[i]) // center left/right/center
                2, 3, 7 -> for (i in textArray.indices) renderElement(x, y + (heightInPixels * i), anchorCorner, textArray[textArray.indices.last - i]) // bottom left/right/center
            }
        }
    }
}