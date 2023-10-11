package com.github.brainage04.togglesprint.config.categories;

import com.github.brainage04.togglesprint.gui.core.RenderGuiData;
import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.observer.Property;
import net.minecraft.client.Minecraft;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public GUIElement toggleSprintElement = new GUIElement(true, 10, Minecraft.getMinecraft().displayHeight - 10, Property.of(RenderGuiData.HorizontalAlignmentType.LEFT), Property.of(RenderGuiData.VerticalAlignmentType.BOTTOM), new OtherSettings(0));

    @Expose
    @ConfigOption(name = "Player Position Tracker", desc = "")
    @Accordion
    public GUIElement playerPositionElement = new GUIElement(true, 10, 10, Property.of(RenderGuiData.HorizontalAlignmentType.LEFT), Property.of(RenderGuiData.VerticalAlignmentType.TOP), new OtherSettings(1));

    @Expose
    @ConfigOption(name = "Player Motion Tracker", desc = "")
    @Accordion
    public GUIElement playerMotionElement = new GUIElement(true, 90, 10, Property.of(RenderGuiData.HorizontalAlignmentType.LEFT), Property.of(RenderGuiData.VerticalAlignmentType.TOP), new OtherSettings(4));

    @Expose
    @ConfigOption(name = "Player Rotation Tracker", desc = "")
    @Accordion
    public GUIElement playerRotationElement = new GUIElement(true, 10, 90, Property.of(RenderGuiData.HorizontalAlignmentType.LEFT), Property.of(RenderGuiData.VerticalAlignmentType.TOP), new OtherSettings(4));

    public static class GUIElement {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables rendering for the element.")
        @ConfigEditorBoolean()
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "X Coordinate", desc = "The X coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1920 * 2, minStep = 1)
        public double x;

        @Expose
        @ConfigOption(name = "Y Coordinate", desc = "The Y coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1080 * 2, minStep = 1)
        public double y;

        @Expose
        @ConfigOption(name = "Horizontal Alignment", desc = "Aligns text to the left, center or right of the element.")
        @ConfigEditorDropdown()
        public Property<RenderGuiData.HorizontalAlignmentType> horizontalAlignment;

        @Expose
        @ConfigOption(name = "Vertical Alignment", desc = "Aligns text to the top, center or bottom of the element.")
        @ConfigEditorDropdown()
        public Property<RenderGuiData.VerticalAlignmentType> verticalAlignment;

        @Expose
        @ConfigOption(name = "Other Settings", desc = "")
        @Accordion
        public OtherSettings otherSettings;

        public GUIElement(boolean isEnabled, double x, double y, Property<RenderGuiData.HorizontalAlignmentType> horizontalAlignment, Property<RenderGuiData.VerticalAlignmentType> verticalAlignment, OtherSettings otherSettings) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
            this.otherSettings = otherSettings;
        }
    }

    public static class OtherSettings {
        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed (if applicable).")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        OtherSettings(int decimals) {
            this.decimals = decimals;
        }
    }
}
