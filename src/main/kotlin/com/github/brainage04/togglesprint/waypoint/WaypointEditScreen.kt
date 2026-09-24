package com.github.brainage04.togglesprint.waypoint

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.BlockPos
import org.lwjgl.input.Keyboard

/** Creates a waypoint, prefilled with the player's position, or edits an existing one. */
class WaypointEditScreen(private val parent: GuiScreen, private val waypoint: Waypoint?) : GuiScreen() {
    private lateinit var nameField: GuiTextField
    private lateinit var xField: GuiTextField
    private lateinit var yField: GuiTextField
    private lateinit var zField: GuiTextField
    private val fields get() = listOf(nameField, xField, yField, zField)
    private var error: String? = null

    override fun initGui() {
        Keyboard.enableRepeatEvents(true)
        val left = (width - FIELD_WIDTH) / 2
        // keep typed values when the window is resized
        val previous = if (::nameField.isInitialized) fields.map { it.text } else null

        nameField = GuiTextField(0, fontRendererObj, left, 60, FIELD_WIDTH, 20)
        nameField.setMaxStringLength(64)
        xField = GuiTextField(1, fontRendererObj, left, 104, COORDINATE_WIDTH, 20)
        yField = GuiTextField(2, fontRendererObj, left + (FIELD_WIDTH - COORDINATE_WIDTH) / 2, 104, COORDINATE_WIDTH, 20)
        zField = GuiTextField(3, fontRendererObj, left + FIELD_WIDTH - COORDINATE_WIDTH, 104, COORDINATE_WIDTH, 20)

        if (previous != null) {
            fields.zip(previous).forEach { (field, text) -> field.text = text }
        } else {
            fillInitialValues()
        }
        nameField.isFocused = true

        buttonList.clear()
        buttonList.add(GuiButton(SAVE, width / 2 - 102, height - 28, 100, 20, "Save"))
        buttonList.add(GuiButton(CANCEL, width / 2 + 2, height - 28, 100, 20, "Cancel"))
    }

    private fun fillInitialValues() {
        if (waypoint != null) {
            nameField.text = waypoint.name
            setPosition(waypoint.x, waypoint.y, waypoint.z)
            return
        }

        nameField.text = WaypointStore.nextQuickName(WaypointStore.current().orEmpty())
        mc.thePlayer?.let { setPosition(BlockPos(it)) }
    }

    private fun setPosition(pos: BlockPos) = setPosition(pos.x, pos.y, pos.z)

    private fun setPosition(x: Int, y: Int, z: Int) {
        xField.text = x.toString()
        yField.text = y.toString()
        zField.text = z.toString()
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun updateScreen() {
        fields.forEach { it.updateCursorCounter() }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_ESCAPE -> mc.displayGuiScreen(parent)
            Keyboard.KEY_RETURN, Keyboard.KEY_NUMPADENTER -> save()
            Keyboard.KEY_TAB -> {
                val fields = fields
                val focused = fields.indexOfFirst { it.isFocused }
                val step = if (isShiftKeyDown()) fields.size - 1 else 1
                val next = (focused + step).mod(fields.size)
                fields.forEachIndexed { index, field -> field.isFocused = index == next }
            }
            else -> fields.forEach { it.textboxKeyTyped(typedChar, keyCode) }
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        fields.forEach { it.mouseClicked(mouseX, mouseY, mouseButton) }
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            SAVE -> save()
            CANCEL -> mc.displayGuiScreen(parent)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, if (waypoint == null) "Create Waypoint" else "Edit Waypoint", width / 2, 20, WHITE)
        drawString(fontRendererObj, "Name", nameField.xPosition, nameField.yPosition - 11, GREY)
        drawString(fontRendererObj, "X", xField.xPosition, xField.yPosition - 11, GREY)
        drawString(fontRendererObj, "Y", yField.xPosition, yField.yPosition - 11, GREY)
        drawString(fontRendererObj, "Z", zField.xPosition, zField.yPosition - 11, GREY)
        fields.forEach { it.drawTextBox() }
        error?.let { drawCenteredString(fontRendererObj, it, width / 2, 140, RED) }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun save() {
        val name = nameField.text.trim()
        val x = xField.text.trim().toIntOrNull()
        val y = yField.text.trim().toIntOrNull()
        val z = zField.text.trim().toIntOrNull()
        WaypointActions.validateName(name)?.let {
            error = it
            return
        }
        if (x == null || y == null || z == null) {
            error = "X, Y and Z must be whole numbers."
            return
        }

        if (waypoint == null) {
            // reports its own problems, e.g. a duplicate name, in chat
            if (WaypointActions.create(name, BlockPos(x, y, z)) == null) {
                error = "Could not create the waypoint; see chat."
                return
            }
        } else {
            val sameName = WaypointStore.find(WaypointStore.current().orEmpty(), name)
            if (sameName != null && sameName !== waypoint) {
                error = "Another waypoint is already called \"$name\"."
                return
            }

            waypoint.name = name
            waypoint.x = x
            waypoint.y = y
            waypoint.z = z
            WaypointStore.save()
        }

        mc.displayGuiScreen(parent)
    }

    private companion object {
        const val WHITE = 0xFFFFFF
        const val GREY = 0xA0A0A0
        const val RED = 0xFF5555
        const val FIELD_WIDTH = 200
        const val COORDINATE_WIDTH = 64

        const val SAVE = 0
        const val CANCEL = 1
    }
}
