package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.Accordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigEditorDropdown;
import io.github.moulberry.moulconfig.annotations.ConfigEditorSlider;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

import java.util.ArrayList;
import java.util.List;

public class BrainageHudParity {
    @Expose
    @ConfigOption(name = "Fishing HUD", desc = "Shows whether the active cast can catch treasure.")
    @Accordion
    public Fishing fishing = new Fishing();

    @Expose
    @ConfigOption(name = "Performance HUD", desc = "Shows client performance measurements.")
    @Accordion
    public Performance performance = new Performance();

    @Expose
    @ConfigOption(name = "Reach HUD", desc = "Shows information about the targeted block or entity.")
    @Accordion
    public Reach reach = new Reach();

    @Expose
    @ConfigOption(name = "Keystrokes HUD", desc = "Shows movement, mouse, and clicks-per-second state.")
    @Accordion
    public Keystrokes keystrokes = new Keystrokes();

    @Expose
    @ConfigOption(name = "Fullbright", desc = "Overrides the dimension's ambient light. Set to 0 to disable.")
    @ConfigEditorSlider(minValue = -1, maxValue = 1, minStep = 0.01F)
    public float fullbright = 0.0F;

    @Expose
    @ConfigOption(name = "Waypoint HUD", desc = "Shows locally saved waypoints in the current dimension.")
    @Accordion
    public Waypoints waypoints = new Waypoints();

    public static class Fishing {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(false, 0, 60, 8);
    }

    public static class Performance {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 99, 0);

        @Expose @ConfigOption(name = "Show FPS", desc = "") @ConfigEditorBoolean
        public boolean showFps = true;
        @Expose @ConfigOption(name = "Show RAM Usage", desc = "") @ConfigEditorBoolean
        public boolean showRamUsage = false;
        @Expose @ConfigOption(name = "Show CPU Usage", desc = "") @ConfigEditorBoolean
        public boolean showCpuUsage = false;
        @Expose @ConfigOption(name = "Show GPU Frame Time", desc = "Uses OpenGL timer queries instead of reporting CPU load as GPU usage.") @ConfigEditorBoolean
        public boolean showGpuUsage = false;
    }

    public static class Reach {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 0, 30, 8);

        @Expose @ConfigOption(name = "Decimal Places", desc = "") @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimalPlaces = 2;
        @Expose @ConfigOption(name = "Show Name", desc = "") @ConfigEditorBoolean
        public boolean showName = true;
        @Expose @ConfigOption(name = "Show Coordinates", desc = "") @ConfigEditorBoolean
        public boolean showCoordinates = true;
        @Expose @ConfigOption(name = "Update On Attack Click", desc = "Refresh only when attack changes from released to pressed.") @ConfigEditorBoolean
        public boolean updateOnAttackClick = false;
    }

    public static class Keystrokes {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, -5, 5, 1);

        @Expose @ConfigOption(name = "Key Backdrop Opacity", desc = "") @ConfigEditorSlider(minValue = 0, maxValue = 255, minStep = 1)
        public int keyBackdropOpacity = 100;
        @Expose @ConfigOption(name = "Show WASD", desc = "") @ConfigEditorBoolean
        public boolean showWasd = true;
        @Expose @ConfigOption(name = "Show Space", desc = "") @ConfigEditorBoolean
        public boolean showSpace = true;
        @Expose @ConfigOption(name = "Show Mouse Buttons", desc = "") @ConfigEditorBoolean
        public boolean showMouseButtons = true;
        @Expose @ConfigOption(name = "Clicks Per Second", desc = "") @ConfigEditorDropdown(values = {"None", "Left Click", "Right Click", "Both"})
        public int clicksPerSecondFormat = 1;
    }

    public static class Waypoints {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(false, 5, 140, 0);

        @Expose @ConfigOption(name = "Maximum Entries", desc = "") @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
        public int maximumEntries = 5;
        @Expose
        public List<Waypoint> entries = new ArrayList<>();
    }

    public static class Waypoint {
        @Expose public String name = "Waypoint";
        @Expose public double x;
        @Expose public double y;
        @Expose public double z;
        @Expose public int dimension;

        public Waypoint() {
        }

        public Waypoint(String name, double x, double y, double z, int dimension) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
    }
}
