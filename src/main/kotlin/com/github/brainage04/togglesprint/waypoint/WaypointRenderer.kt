package com.github.brainage04.togglesprint.waypoint

import com.github.brainage04.togglesprint.config.categories.BrainageHudParity
import com.github.brainage04.togglesprint.utils.ConfigUtils
import io.github.moulberry.moulconfig.ChromaColour
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.util.Locale
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Draws the current dimension's visible waypoints in the world: a tinted beacon beam, a spinning
 * gem hovering above the spot with a pulse spreading over the ground beneath it, and a label with
 * the name and distance that stays readable through walls.
 *
 * Everything is drawn relative to the camera, whose position is the render view entity's
 * interpolated eye position.
 */
object WaypointRenderer {
    /**
     * Waypoints further than this share of the render distance are drawn scaled towards the
     * camera. Scaling about the camera leaves a waypoint's size and place on screen unchanged, but
     * keeps it inside the far plane and out of the distance fog.
     */
    private const val PROJECTION_SHARE = 0.5
    /** Past these distances the marker and label grow with distance, so they keep their size on screen. */
    private const val MARKER_GROWTH_START = 24.0
    private const val LABEL_GROWTH_START = 12.0
    /** Within this distance every label shows its name. */
    private const val NAME_DISTANCE = 16.0
    /** How close to the crosshair (in degrees) a waypoint must be to show its name from afar. */
    private const val FOCUS_ANGLE = 5.0
    private const val TEXT_SCALE = 0.025f
    private const val PULSE_SEGMENTS = 48
    private const val PULSE_DISTANCE = 48.0
    private const val TWO_PI = (Math.PI * 2).toFloat()
    private val beamTexture = ResourceLocation("textures/entity/beacon_beam.png")

    /** The camera and settings shared by every waypoint drawn in one frame. */
    private class Frame(
        val config: BrainageHudParity.Waypoints,
        val font: FontRenderer,
        val time: Float,
        val projectionDistance: Double,
        val worldHeight: Int,
        val eyeX: Double,
        val eyeY: Double,
        val eyeZ: Double,
        /** How far the eye is above the origin of the world's model view (the entity's feet). */
        val eyeHeight: Double,
        val lookX: Double,
        val lookY: Double,
        val lookZ: Double,
        val viewYaw: Float,
        val viewPitch: Float,
    )

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        val config = ConfigUtils.brainageHudParity.waypoints
        if (!config.showInWorld) return

        val minecraft = Minecraft.getMinecraft()
        val world = minecraft.theWorld ?: return
        val camera = minecraft.renderViewEntity ?: return

        val dimension = world.provider.dimensionId
        val waypoints = WaypointStore.current()?.filter { it.visible && it.dimension == dimension }.orEmpty()
        val drawn = if (config.showWorldCentre) waypoints + worldCentre(dimension, config.worldCentreColour) else waypoints
        if (drawn.isEmpty()) return

        val partialTicks = event.partialTicks
        val feetX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks
        val feetY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks
        val feetZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks
        val eyeHeight = camera.eyeHeight.toDouble()
        val look = camera.getLook(partialTicks)
        val frame = Frame(
            config,
            minecraft.fontRendererObj,
            Math.floorMod(world.totalWorldTime, 24_000L) + partialTicks,
            max(32.0, minecraft.gameSettings.renderDistanceChunks * 16.0 * PROJECTION_SHARE),
            world.height,
            feetX, feetY + eyeHeight, feetZ,
            eyeHeight,
            look.xCoord, look.yCoord, look.zCoord,
            minecraft.renderManager.playerViewY,
            minecraft.renderManager.playerViewX,
        )

        val state = SavedGlState()
        GlStateManager.disableLighting()
        GlStateManager.disableFog()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)

        // furthest first, so nearer translucent parts are blended over further ones
        for (waypoint in drawn.sortedByDescending { distanceSquared(frame, it) }) {
            drawWaypoint(frame, waypoint)
        }

        state.restore()
    }

    /** The built-in waypoint at 0, 64, 0 that every dimension has. */
    private fun worldCentre(dimension: Int, colour: String): Waypoint {
        val rgb = runCatching { ChromaColour.specialToChromaRGB(colour) }.getOrDefault(0xFFFFFF) and 0xFFFFFF
        return Waypoint("World Centre", 0, 64, 0, dimension, rgb)
    }

    private fun distanceSquared(frame: Frame, waypoint: Waypoint): Double {
        val x = waypoint.x + 0.5 - frame.eyeX
        val y = waypoint.y - frame.eyeY
        val z = waypoint.z + 0.5 - frame.eyeZ
        return x * x + y * y + z * z
    }

    private fun drawWaypoint(frame: Frame, waypoint: Waypoint) {
        // where a player standing on the waypoint's block has their feet, relative to the eye
        val offsetX = waypoint.x + 0.5 - frame.eyeX
        val offsetY = waypoint.y - frame.eyeY
        val offsetZ = waypoint.z + 0.5 - frame.eyeZ
        val horizontalDistance = sqrt(offsetX * offsetX + offsetZ * offsetZ)
        val distance = sqrt(horizontalDistance * horizontalDistance + offsetY * offsetY)
        val rgb = waypoint.colour and 0xFFFFFF
        // standing on the waypoint: its gem and label would sit in the player's face
        val standingOnIt = horizontalDistance < 1.0 && offsetY > -2.5 && offsetY < 0.5

        val markerSize = max(1.0, distance / MARKER_GROWTH_START).toFloat()
        val bob = 0.12f * MathHelper.sin(frame.time * 0.08f)
        val gemHalfHeight = 0.36f * markerSize
        val gemCentre = 1.1f + (1.3f + bob) * markerSize

        GlStateManager.pushMatrix()
        // scale about the eye rather than the model view's origin at the feet
        GlStateManager.translate(0.0, frame.eyeHeight, 0.0)
        val projection = if (distance > frame.projectionDistance) (frame.projectionDistance / distance).toFloat() else 1.0f
        GlStateManager.scale(projection, projection, projection)
        GlStateManager.translate(offsetX, offsetY, offsetZ)

        if (frame.config.showBeams && horizontalDistance > 1.5) {
            drawBeam(frame, waypoint.y, rgb, horizontalDistance)
        }
        if (frame.config.showMarkers) {
            if (distance < PULSE_DISTANCE) drawPulse(frame.time, rgb)
            if (!standingOnIt) drawGem(frame.time, rgb, gemCentre, gemHalfHeight)
        }
        if (frame.config.showLabels && !standingOnIt) {
            val height = gemCentre + gemHalfHeight + 0.3f * markerSize
            val focused = isFocused(frame, offsetX, offsetY + height, offsetZ)
            drawLabel(frame, waypoint.name, rgb, distance, height, focused)
        }

        GlStateManager.popMatrix()
    }

    /**
     * A slim beacon beam from the bottom of the world to the top; like vanilla's, the inner beam
     * spins and scrolls inside a faint glow. It widens with distance so it stays visible.
     */
    private fun drawBeam(frame: Frame, waypointY: Int, rgb: Int, horizontalDistance: Double) {
        val widen = max(1.0, horizontalDistance / 64.0)
        val bottom = (-waypointY).toDouble()
        val top = (frame.worldHeight - waypointY).toDouble()
        val height = top - bottom
        val red = (rgb shr 16 and 0xFF) / 255.0f
        val green = (rgb shr 8 and 0xFF) / 255.0f
        val blue = (rgb and 0xFF) / 255.0f
        val scroll = (-frame.time * 0.2 - floor(-frame.time * 0.1)).let { it - floor(it) }

        Minecraft.getMinecraft().textureManager.bindTexture(beamTexture)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT)
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableCull()
        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        // no depth writes: the label sits on the beam and must not be hidden by it
        GlStateManager.depthMask(false)

        val innerRadius = 0.09 * widen
        val innerAngle = frame.time * 0.025 * -1.5
        val innerV = -1.0 + scroll
        // additive, like vanilla's inner beam
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO)
        drawBeamPrism(innerRadius, innerAngle + Math.PI * 0.25, bottom, top, innerV, height * (0.5 / innerRadius) + innerV, red, green, blue, 1.0f)

        val glowRadius = 0.15 * widen * Math.sqrt(2.0)
        val glowV = -1.0 + scroll
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        drawBeamPrism(glowRadius, Math.PI * 0.25, bottom, top, glowV, height + glowV, red, green, blue, 0.125f)
    }

    /** The four textured sides of a square prism with corners [radius] from its axis. */
    private fun drawBeamPrism(
        radius: Double, angle: Double, bottom: Double, top: Double, vBottom: Double, vTop: Double,
        red: Float, green: Float, blue: Float, alpha: Float,
    ) {
        val tessellator = Tessellator.getInstance()
        val renderer = tessellator.worldRenderer
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        for (side in 0 until 4) {
            val from = angle + side * Math.PI * 0.5
            val to = from + Math.PI * 0.5
            val x0 = cos(from) * radius
            val z0 = sin(from) * radius
            val x1 = cos(to) * radius
            val z1 = sin(to) * radius
            renderer.pos(x0, top, z0).tex(1.0, vTop).color(red, green, blue, alpha).endVertex()
            renderer.pos(x0, bottom, z0).tex(1.0, vBottom).color(red, green, blue, alpha).endVertex()
            renderer.pos(x1, bottom, z1).tex(0.0, vBottom).color(red, green, blue, alpha).endVertex()
            renderer.pos(x1, top, z1).tex(0.0, vTop).color(red, green, blue, alpha).endVertex()
        }
        tessellator.draw()
    }

    /** An octahedron that spins and bobs, lit from above: bright top facets, dark lower ones. */
    private fun drawGem(time: Float, rgb: Int, centre: Float, halfHeight: Float) {
        val radius = halfHeight * 0.7f
        val spin = Math.toRadians(time * 2.5).toFloat()
        val shades = intArrayOf(
            lerpRgb(rgb, 0xFFFFFF, 0.45f),
            lerpRgb(rgb, 0xFFFFFF, 0.15f),
            scaleRgb(rgb, 0.75f),
            scaleRgb(rgb, 0.5f),
        )
        val xs = FloatArray(4)
        val zs = FloatArray(4)
        for (corner in 0 until 4) {
            val angle = spin + corner * TWO_PI / 4
            xs[corner] = MathHelper.cos(angle) * radius
            zs[corner] = MathHelper.sin(angle) * radius
        }

        GlStateManager.disableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableCull()
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

        val tessellator = Tessellator.getInstance()
        val renderer = tessellator.worldRenderer
        renderer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR)
        for (corner in 0 until 4) {
            val next = (corner + 1) % 4
            val alternate = corner % 2
            // counter-clockwise seen from outside, so back faces are culled
            val top = shades[alternate]
            vertex(renderer, 0.0f, centre + halfHeight, 0.0f, top, 235)
            vertex(renderer, xs[next], centre, zs[next], top, 235)
            vertex(renderer, xs[corner], centre, zs[corner], top, 235)
            val bottom = shades[2 + alternate]
            vertex(renderer, 0.0f, centre - halfHeight, 0.0f, bottom, 235)
            vertex(renderer, xs[corner], centre, zs[corner], bottom, 235)
            vertex(renderer, xs[next], centre, zs[next], bottom, 235)
        }
        tessellator.draw()
    }

    /** Two rings spreading over the ground and fading, half a cycle apart, like a sonar ping. */
    private fun drawPulse(time: Float, rgb: Int) {
        GlStateManager.disableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.disableCull()
        GlStateManager.disableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

        val colour = lerpRgb(rgb, 0xFFFFFF, 0.3f)
        val tessellator = Tessellator.getInstance()
        val renderer = tessellator.worldRenderer
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        for (ring in 0 until 2) {
            val progress = (time / 50.0f + ring * 0.5f).let { it - floor(it) }
            val outer = 0.3f + 1.5f * progress
            val inner = max(0.0f, outer - 0.06f - 0.1f * progress)
            val fade = 1.0f - progress
            val alpha = (200 * fade * fade).toInt()
            val y = 0.02f
            for (segment in 0 until PULSE_SEGMENTS) {
                val from = segment * TWO_PI / PULSE_SEGMENTS
                val to = (segment + 1) * TWO_PI / PULSE_SEGMENTS
                vertex(renderer, MathHelper.cos(from) * inner, y, MathHelper.sin(from) * inner, colour, alpha)
                vertex(renderer, MathHelper.cos(from) * outer, y, MathHelper.sin(from) * outer, colour, alpha)
                vertex(renderer, MathHelper.cos(to) * outer, y, MathHelper.sin(to) * outer, colour, alpha)
                vertex(renderer, MathHelper.cos(to) * inner, y, MathHelper.sin(to) * inner, colour, alpha)
            }
        }
        tessellator.draw()
    }

    /**
     * The name (in the waypoint's colour) over the distance, facing the camera. It is drawn dimly
     * through walls and brightly where nothing is in front of it, like a vanilla name tag, and grows
     * with distance so that it keeps its size on screen.
     */
    private fun drawLabel(frame: Frame, name: String, rgb: Int, distance: Double, height: Float, focused: Boolean) {
        val showName = frame.config.alwaysShowNames || distance < NAME_DISTANCE || focused
        val scale = TEXT_SCALE * max(1.0, distance / LABEL_GROWTH_START).toFloat() * frame.config.labelScalePercent / 100.0f
        val font = frame.font
        val lineHeight = font.FONT_HEIGHT
        val distanceText = formatDistance(distance)

        GlStateManager.pushMatrix()
        GlStateManager.translate(0.0f, height, 0.0f)
        GlStateManager.rotate(-frame.viewYaw, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(frame.viewPitch, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(-scale, -scale, scale)
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GlStateManager.enableAlpha()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003f)

        val lines = if (showName) listOf(name to rgb, distanceText to 0xE0E0E0) else listOf(distanceText to rgb)
        val top = -lines.size * lineHeight - (lines.size - 1)
        val halfWidth = lines.maxOf { font.getStringWidth(it.first) } / 2.0f

        // through walls: a dim copy over a backdrop, drawn without the depth test
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.disableTexture2D()
        val tessellator = Tessellator.getInstance()
        val renderer = tessellator.worldRenderer
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
        vertex(renderer, -halfWidth - 1, top - 1.0f, 0.0f, 0, 96)
        vertex(renderer, -halfWidth - 1, 0.0f, 0.0f, 0, 96)
        vertex(renderer, halfWidth + 1, 0.0f, 0.0f, 0, 96)
        vertex(renderer, halfWidth + 1, top - 1.0f, 0.0f, 0, 96)
        tessellator.draw()
        GlStateManager.enableTexture2D()
        drawLines(font, lines, top, 110 shl 24)

        // unobstructed: a bright copy, drawn with the depth test
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        drawLines(font, lines, top, 0xFF shl 24)

        GlStateManager.popMatrix()
    }

    private fun drawLines(font: FontRenderer, lines: List<Pair<String, Int>>, top: Int, alpha: Int) {
        var y = top
        for ((text, rgb) in lines) {
            font.drawString(text, -font.getStringWidth(text) / 2.0f, y.toFloat(), alpha or rgb, false)
            y += font.FONT_HEIGHT + 1
        }
    }

    private fun isFocused(frame: Frame, x: Double, y: Double, z: Double): Boolean {
        val length = sqrt(x * x + y * y + z * z)
        if (length == 0.0) return true
        val cosine = (x * frame.lookX + y * frame.lookY + z * frame.lookZ) / length
        return cosine > cos(Math.toRadians(FOCUS_ANGLE))
    }

    /** Metres below a kilometre, then kilometres to one decimal place. */
    fun formatDistance(distance: Double): String {
        val metres = Math.round(distance)
        if (metres < 1000L) return "$metres m"
        return String.format(Locale.ROOT, "%.1f km", distance / 1000.0)
    }

    private fun vertex(renderer: net.minecraft.client.renderer.WorldRenderer, x: Float, y: Float, z: Float, rgb: Int, alpha: Int) {
        renderer.pos(x.toDouble(), y.toDouble(), z.toDouble())
            .color(rgb shr 16 and 0xFF, rgb shr 8 and 0xFF, rgb and 0xFF, alpha)
            .endVertex()
    }

    private fun lerpRgb(from: Int, to: Int, amount: Float): Int {
        fun channel(shift: Int): Int {
            val a = from shr shift and 0xFF
            val b = to shr shift and 0xFF
            return (a + (b - a) * amount).toInt() shl shift
        }
        return channel(16) or channel(8) or channel(0)
    }

    private fun scaleRgb(rgb: Int, factor: Float): Int = lerpRgb(rgb, 0, 1.0f - factor)

    /**
     * The GL state this renderer changes, read before drawing and put back afterwards through
     * GlStateManager so its cache stays in step.
     */
    private class SavedGlState {
        private val lighting = GL11.glIsEnabled(GL11.GL_LIGHTING)
        private val texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        private val blend = GL11.glIsEnabled(GL11.GL_BLEND)
        private val depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST)
        private val cull = GL11.glIsEnabled(GL11.GL_CULL_FACE)
        private val fog = GL11.glIsEnabled(GL11.GL_FOG)
        private val alpha = GL11.glIsEnabled(GL11.GL_ALPHA_TEST)
        private val depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)
        private val lightmap: Boolean

        init {
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            lightmap = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        }

        fun restore() {
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
            if (lightmap) GlStateManager.enableTexture2D() else GlStateManager.disableTexture2D()
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
            if (lighting) GlStateManager.enableLighting() else GlStateManager.disableLighting()
            if (texture) GlStateManager.enableTexture2D() else GlStateManager.disableTexture2D()
            if (blend) GlStateManager.enableBlend() else GlStateManager.disableBlend()
            if (depth) GlStateManager.enableDepth() else GlStateManager.disableDepth()
            if (cull) GlStateManager.enableCull() else GlStateManager.disableCull()
            if (fog) GlStateManager.enableFog() else GlStateManager.disableFog()
            if (alpha) GlStateManager.enableAlpha() else GlStateManager.disableAlpha()
            GlStateManager.depthMask(depthMask)
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }
}
