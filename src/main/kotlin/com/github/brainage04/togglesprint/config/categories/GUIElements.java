package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public ToggleSprintElement toggleSprintElement = new ToggleSprintElement(new CoreSettings(true, 5, 3, 2), false);

    @Expose
    @ConfigOption(name = "Position Display", desc = "")
    @Accordion
    public PositionTracker positionTracker = new PositionTracker(new CoreSettings(true, 10, 10, 0), 1, true, true, true);

    @Expose
    @ConfigOption(name = "Rotation Display", desc = "")
    @Accordion
    public RotationTracker rotationTracker = new RotationTracker(new CoreSettings(true, 10, 120, 0), 2, false, false, false);

    @Expose
    @ConfigOption(name = "Motion Display", desc = "")
    @Accordion
    public MotionTracker motionTracker = new MotionTracker(new CoreSettings(false, 80, 10, 0), 2, false);

    @Expose
    @ConfigOption(name = "Date/Time Display", desc = "")
    @Accordion
    public RealTimeTracker realTimeTracker = new RealTimeTracker(new CoreSettings(true, 10, 10, 1), 1, true, true);

    @Expose
    @ConfigOption(name = "Ping", desc = "")
    @Accordion
    public LagTracker pingTracker = new LagTracker(new CoreSettings(true, 10, 80, 0), true);

    @Expose
    @ConfigOption(name = "TPS", desc = "")
    @Accordion
    public LagTracker tpsTracker = new LagTracker(new CoreSettings(false, 10, 100, 0), true);

    @Expose
    @ConfigOption(name = "Entity Tracker", desc = "")
    @Accordion
    public EntityTracker entityTracker = new EntityTracker(new CoreSettings(false, 130, 10, 1), true, true, true, true, true);

/*
    public static class ExampleElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        public ExampleElement(CoreSettings coreSettings) {
            this.coreSettings = coreSettings;
        }
    }
 */

    public static class ToggleSprintElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Internal Values", desc = "Show internal values used for toggle sprint/sneak (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showInternalValues;

        public ToggleSprintElement(CoreSettings coreSettings, boolean showInternalValues) {
            this.coreSettings = coreSettings;
            this.showInternalValues = showInternalValues;
        }
    }

    public static class PositionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show Facing", desc = "Show the direction that the player is facing.")
        @ConfigEditorBoolean
        public boolean showFacing;

        @Expose
        @ConfigOption(name = "C Counter", desc = "Shows the number of loaded cubic chunks (16x16x16) containing air blocks (excluding 0% and 100% air chunks) within the player's viewport.")
        @ConfigEditorBoolean
        public boolean showChunkCounter;

        @Expose
        @ConfigOption(name = "E Counter", desc = "Shows the number of rendered entities within the player's viewport (even through walls, unless you are culling them using a mod such as Patcher).")
        @ConfigEditorBoolean
        public boolean showEntityCounter;

        public PositionTracker(CoreSettings coreSettings, int decimals, boolean showFacing, boolean showChunkCounter, boolean showEntityCounter) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.showFacing = showFacing;
            this.showChunkCounter = showChunkCounter;
            this.showEntityCounter = showEntityCounter;
        }
    }

    public static class RotationTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Compact Format", desc = "Compacts the format to match that of the F3 debug menu.")
        @ConfigEditorBoolean
        public boolean compactFormat;

        @Expose
        @ConfigOption(name = "Show True Yaw", desc = "Shows the player's true yaw value (If different from the clamped value. Useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueYaw;

        @Expose
        @ConfigOption(name = "Only Show with Farming Tool", desc = "Only shows when the player is holding a farming tool. Compatible with Hypixel Skyblock farming equipment.")
        @ConfigEditorBoolean
        public boolean dependOnFarmingTool;

        public RotationTracker(CoreSettings coreSettings, int decimals, boolean compactFormat, boolean showTrueYaw,  boolean dependOnFarmingTool) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.compactFormat = compactFormat;
            this.showTrueYaw = showTrueYaw;
            this.dependOnFarmingTool = dependOnFarmingTool;
        }
    }

    public static class MotionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show True Motion", desc = "Also shows the player's motion in blocks (metres) per tick (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueMotion;

        public MotionTracker(CoreSettings coreSettings, int decimals, boolean showTrueMotion) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.showTrueMotion = showTrueMotion;
        }
    }

    public static class RealTimeTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Time Format", desc = "Switches between 12 and 24 hour format. Select \"None\" to remove the time from this element.")
        @ConfigEditorDropdown(values = {"12-hour", "24-hour", "None"})
        public int timeFormat;

        @Expose
        @ConfigOption(name = "Include Date", desc = "Appends your computer's date to the start of the element.")
        @ConfigEditorBoolean
        public boolean includeDate;

        @Expose
        @ConfigOption(name = "Include Timezone", desc = "Appends your computer's timezone to the end of the element.")
        @ConfigEditorBoolean
        public boolean includeTimezone;

        public RealTimeTracker(CoreSettings coreSettings, int timeFormat, boolean includeDate, boolean includeTimezone) {
            this.coreSettings = coreSettings;
            this.timeFormat = timeFormat;
            this.includeDate = includeDate;
            this.includeTimezone = includeTimezone;
        }
    }

    public static class LagTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Color", desc = "Color the number based on how high or low it is (green = low, red = high).")
        @ConfigEditorBoolean
        public boolean showColor;

        public LagTracker(CoreSettings coreSettings, boolean showColor) {
            this.coreSettings = coreSettings;
            this.showColor = showColor;
        }
    }

    public static class EntityTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Creatures", desc = "Show non-hostile land mobs (animals) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showCreatures;

        @Expose
        @ConfigOption(name = "Show Water Creatures", desc = "Show non-hostile water mobs (marine life) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showWaterCreatures;

        @Expose
        @ConfigOption(name = "Show Ambients", desc = "Show ambient mobs (bats) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showAmbients;

        @Expose
        @ConfigOption(name = "Show Monsters", desc = "Show hostile mobs (monsters) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showMonsters;

        @Expose
        @ConfigOption(name = "Show Others", desc = "Show the count of non-categorised mobs (armour stands, items, etc.) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showOthers;

        public EntityTracker(CoreSettings coreSettings, boolean showCreatures, boolean showWaterCreatures, boolean showAmbients, boolean showMonsters, boolean showOthers) {
            this.coreSettings = coreSettings;
            this.showCreatures = showCreatures;
            this.showWaterCreatures = showWaterCreatures;
            this.showAmbients = showAmbients;
            this.showMonsters = showMonsters;
            this.showOthers = showOthers;
        }
    }

    public static class CoreSettings {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables rendering for the element.")
        @ConfigEditorBoolean()
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "X Coordinate", desc = "The X coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = -1_920, maxValue = 1_920, minStep = 1)
        public double x;

        @Expose
        @ConfigOption(name = "Y Coordinate", desc = "The Y coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = -1_080, maxValue = 1_080, minStep = 1)
        public double y;

        @Expose
        @ConfigOption(name = "Anchor Corner", desc = "Aligns text to a corner of the screen.")
        @ConfigEditorDropdown(values = {"Top Left", "Top Right", "Bottom Left", "Bottom Right", "Center Left", "Center Right", "Center Top", "Center Bottom", "Center"})
        public int anchorCorner;

        public CoreSettings(boolean isEnabled, int x, int y, int anchorCorner) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.anchorCorner = anchorCorner;
        }
    }
}
