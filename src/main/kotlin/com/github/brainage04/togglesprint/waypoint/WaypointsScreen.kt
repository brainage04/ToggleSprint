package com.github.brainage04.togglesprint.waypoint

import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot

/** Lists the current world's waypoints, with buttons to add, edit, show/hide and delete them. */
class WaypointsScreen(private val parent: GuiScreen?) : GuiScreen() {
    private var waypoints: MutableList<Waypoint>? = null
    private var selected = -1
    private lateinit var list: WaypointList
    private lateinit var editButton: GuiButton
    private lateinit var toggleButton: GuiButton
    private lateinit var deleteButton: GuiButton

    override fun initGui() {
        waypoints = WaypointStore.current()
        if (selected >= (waypoints?.size ?: 0)) selected = -1

        list = WaypointList()
        buttonList.clear()
        val rowWidth = BUTTON_WIDTH * 4 + BUTTON_GAP * 3
        val left = (width - rowWidth) / 2
        val rowY = height - 56
        val addButton = GuiButton(ADD, left, rowY, BUTTON_WIDTH, 20, "Add")
        addButton.enabled = waypoints != null
        editButton = GuiButton(EDIT, left + (BUTTON_WIDTH + BUTTON_GAP), rowY, BUTTON_WIDTH, 20, "Edit")
        toggleButton = GuiButton(TOGGLE, left + (BUTTON_WIDTH + BUTTON_GAP) * 2, rowY, BUTTON_WIDTH, 20, "Hide")
        deleteButton = GuiButton(DELETE, left + (BUTTON_WIDTH + BUTTON_GAP) * 3, rowY, BUTTON_WIDTH, 20, "Delete")
        buttonList.add(addButton)
        buttonList.add(editButton)
        buttonList.add(toggleButton)
        buttonList.add(deleteButton)
        buttonList.add(GuiButton(DONE, (width - 200) / 2, height - 28, 200, 20, "Done"))
        updateButtons()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        list.handleMouseInput()
    }

    override fun actionPerformed(button: GuiButton) {
        if (!button.enabled) return
        when (button.id) {
            ADD -> mc.displayGuiScreen(WaypointEditScreen(this, null))
            EDIT -> selectedWaypoint()?.let { mc.displayGuiScreen(WaypointEditScreen(this, it)) }
            TOGGLE -> selectedWaypoint()?.let {
                it.visible = !it.visible
                WaypointStore.save()
                updateButtons()
            }
            DELETE -> selectedWaypoint()?.let {
                WaypointActions.remove(it.name)
                selected = -1
                updateButtons()
            }
            DONE -> mc.displayGuiScreen(parent)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        list.drawScreen(mouseX, mouseY, partialTicks)
        drawCenteredString(fontRendererObj, "Waypoints", width / 2, 12, WHITE)

        val waypoints = waypoints
        if (waypoints == null) {
            drawCenteredString(fontRendererObj, "Waypoints are kept per world. Join a world to manage them.", width / 2, height / 2 - 20, GREY)
        } else if (waypoints.isEmpty()) {
            drawCenteredString(fontRendererObj, "No waypoints yet. Press Add, or the Create Waypoint key in game.", width / 2, height / 2 - 20, GREY)
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun selectedWaypoint(): Waypoint? = waypoints?.getOrNull(selected)

    private fun updateButtons() {
        val waypoint = selectedWaypoint()
        editButton.enabled = waypoint != null
        toggleButton.enabled = waypoint != null
        deleteButton.enabled = waypoint != null
        toggleButton.displayString = if (waypoint?.visible != false) "Hide" else "Show"
    }

    private inner class WaypointList : GuiSlot(mc, width, height, 32, height - 64, 24) {
        override fun getSize(): Int = waypoints?.size ?: 0

        override fun elementClicked(slotIndex: Int, isDoubleClick: Boolean, mouseX: Int, mouseY: Int) {
            selected = slotIndex
            updateButtons()
            if (isDoubleClick) selectedWaypoint()?.let { mc.displayGuiScreen(WaypointEditScreen(this@WaypointsScreen, it)) }
        }

        override fun isSelected(slotIndex: Int): Boolean = slotIndex == selected

        override fun drawBackground() {}

        override fun getListWidth(): Int = 300

        override fun getScrollBarX(): Int = width / 2 + 154

        override fun drawSlot(entryID: Int, x: Int, y: Int, slotHeight: Int, mouseXIn: Int, mouseYIn: Int) {
            val waypoint = waypoints?.getOrNull(entryID) ?: return
            val font = fontRendererObj
            font.drawString(waypoint.name, x + 2, y + 1, 0xFF000000.toInt() or waypoint.colour)
            if (!waypoint.visible) font.drawString("(hidden)", x + 2 + font.getStringWidth(waypoint.name) + 6, y + 1, GREY)
            font.drawString("${waypoint.coordinates()}  ${WaypointActions.dimensionName(waypoint.dimension)}", x + 2, y + 12, GREY)
        }
    }

    private companion object {
        const val WHITE = 0xFFFFFF
        const val GREY = 0xA0A0A0
        const val BUTTON_WIDTH = 74
        const val BUTTON_GAP = 4

        const val ADD = 0
        const val EDIT = 1
        const val TOGGLE = 2
        const val DELETE = 3
        const val DONE = 4
    }
}
