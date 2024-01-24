package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public ToggleSprintElement toggleSprintElement = new ToggleSprintElement(new CoreSettings(true, 5, 3, 2), false);

    @Expose
    @ConfigOption(name = "Position Tracker", desc = "")
    @Accordion
    public PositionTracker positionTracker = new PositionTracker(new CoreSettings(true, 10, 10, 0), 1, true, true);

    @Expose
    @ConfigOption(name = "Rotation Tracker", desc = "")
    @Accordion
    public RotationTracker rotationTracker = new RotationTracker(new CoreSettings(true, 10, 110, 0), 2, false, false);

    @Expose
    @ConfigOption(name = "Motion Tracker", desc = "")
    @Accordion
    public MotionTracker motionTracker = new MotionTracker(new CoreSettings(false, 80, 10, 0), 2, false);

    @Expose
    @ConfigOption(name = "Equipment Tracker", desc = "")
    @Accordion
    public EquipmentTracker equipmentTracker = new EquipmentTracker(new CoreSettings(true, 10, 10, 3), 1, true, 0, 0, 1);

    @Expose
    @ConfigOption(name = "Projectile Tracker", desc = "")
    @Accordion
    public ItemTracker projectileTracker = new ItemTracker(new CoreSettings(true, 10, 80, 3), 1);

    @Expose
    @ConfigOption(name = "Food Tracker", desc = "")
    @Accordion
    public ItemTracker foodTracker = new ItemTracker(new CoreSettings(true, 10, 140, 3), 1);

    @Expose
    @ConfigOption(name = "Real Time Tracker", desc = "")
    @Accordion
    public RealTimeTracker realTimeTracker = new RealTimeTracker(new CoreSettings(true, 10, 10, 1), 1, true, true);

    @Expose
    @ConfigOption(name = "Ping Tracker", desc = "")
    @Accordion
    public LagTracker pingTracker = new LagTracker(new CoreSettings(true, 10, 70, 0), true);

    @Expose
    @ConfigOption(name = "TPS Tracker", desc = "")
    @Accordion
    public LagTracker tpsTracker = new LagTracker(new CoreSettings(false, 10, 90, 0), true);

    @Expose
    @ConfigOption(name = "Entity Tracker", desc = "")
    @Accordion
    public EntityTracker entityTracker = new EntityTracker(new CoreSettings(false, 10, 50, 1), true, true, true, true, true);

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
        @ConfigOption(name = "C Counter", desc = "Shows the number of loaded cubic chunks (16x16x16) containing air blocks (excluding 0% and 100% air chunks) within the player's viewport.")
        @ConfigEditorBoolean
        public boolean showChunkCounter;

        @Expose
        @ConfigOption(name = "E Counter", desc = "Shows the number of rendered entities within the player's viewport (even through walls, unless you are culling them using a mod such as Patcher).")
        @ConfigEditorBoolean
        public boolean showEntityCounter;

        public PositionTracker(CoreSettings coreSettings, int decimals, boolean showChunkCounter, boolean showEntityCounter) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
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
        @ConfigOption(name = "Show True Yaw", desc = "Shows the player's true yaw value (If different from the clamped value. Useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueYaw;

        @Expose
        @ConfigOption(name = "Only Show with Farming Tool", desc = "Only shows when the player is holding a farming tool. Compatible with Hypixel Skyblock farming equipment.")
        @ConfigEditorBoolean
        public boolean dependOnFarmingTool;

        public RotationTracker(CoreSettings coreSettings, int decimals, boolean showTrueYaw,  boolean dependOnFarmingTool) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
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

    public static class EquipmentTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat;

        @Expose
        @ConfigOption(name = "Display Durability Bar", desc = "If \"Prefix Format\" is set to \"Icon\", display the durability bar of the item.")
        @ConfigEditorBoolean
        public boolean displayDurabilityBar;

        @Expose
        @ConfigOption(name = "Name Format", desc = "If \"Prefix Format\" is set to \"Name\", switch between displaying the slot or item name.")
        @ConfigEditorDropdown(values = {"Item Name", "Slot Name"})
        public int nameFormat;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Durability Format", desc = "Switch between displaying the durability as a percentage, fraction or number (fraction without the denominator).")
        @ConfigEditorDropdown(values = {"Percentage", "Fraction", "Number"})
        public int durabilityFormat;

        public EquipmentTracker(CoreSettings coreSettings, int prefixFormat, boolean displayDurabilityBar, int nameFormat, int durabilityFormat, int decimals) {
            this.coreSettings = coreSettings;
            this.prefixFormat = prefixFormat;
            this.displayDurabilityBar = displayDurabilityBar;
            this.nameFormat = nameFormat;
            this.decimals = decimals;
            this.durabilityFormat = durabilityFormat;
        }
    }

    public static class ItemTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat;

        public ItemTracker(CoreSettings coreSettings, int prefixFormat) {
            this.coreSettings = coreSettings;
            this.prefixFormat = prefixFormat;
        }
    }

    public static class RealTimeTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "24 Hour Format", desc = "Switches to 24-hour format instead of 12.")
        @ConfigEditorDropdown(values = {"None", "12-hour", "24-hour"})
        public int dateFormat;

        @Expose
        @ConfigOption(name = "Include Date", desc = "Appends today's date to the start of the element.")
        @ConfigEditorBoolean
        public boolean includeDate;

        @Expose
        @ConfigOption(name = "Include Timezone", desc = "Appends your computer's timezone to the end of the element.")
        @ConfigEditorBoolean
        public boolean includeTimezone;

        public RealTimeTracker(CoreSettings coreSettings, int dateFormat, boolean includeDate, boolean includeTimezone) {
            this.coreSettings = coreSettings;
            this.dateFormat = dateFormat;
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
