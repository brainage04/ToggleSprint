package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryTrackers {
    @Expose
    @ConfigOption(name = "Equipment Tracker", desc = "")
    @Accordion
    public EquipmentTracker equipmentTracker = new EquipmentTracker(new GUIElements.CoreSettings(true, 10, 10, 3), 1, true, 0, 1);

    @Expose
    @ConfigOption(name = "Projectile Tracker", desc = "")
    @Accordion
    public ProjectileTracker projectileTracker = new ProjectileTracker(new GUIElements.CoreSettings(true, 10, 50, 1), 1, true, new ArrayList<>(Arrays.asList(0, 1, 2, 3)));

    @Expose
    @ConfigOption(name = "Food Tracker", desc = "")
    @Accordion
    public FoodTracker foodTracker = new FoodTracker(new GUIElements.CoreSettings(true, 10, 80, 3), 1, true, new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)));

    public static class EquipmentTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat;

        @Expose
        @ConfigOption(name = "Display Durability Bar", desc = "If \"Prefix Format\" is set to \"Icon\", display the durability bar of the item.")
        @ConfigEditorBoolean
        public boolean displayDurabilityBar;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Durability Format", desc = "Switch between displaying the durability as a percentage, fraction or number (fraction without the denominator).")
        @ConfigEditorDropdown(values = {"Percentage", "Fraction", "Number"})
        public int durabilityFormat;

        public EquipmentTracker(GUIElements.CoreSettings coreSettings, int prefixFormat, boolean displayDurabilityBar, int durabilityFormat, int decimals) {
            this.coreSettings = coreSettings;
            this.prefixFormat = prefixFormat;
            this.displayDurabilityBar = displayDurabilityBar;
            this.decimals = decimals;
            this.durabilityFormat = durabilityFormat;
        }
    }

    public static class ProjectileTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat;

        @Expose
        @ConfigOption(name = "Print Arrays", desc = "If items are contained in more than 1 slot, show the number in each slot in order (hotbar from left to right, then inventory rows 1, 2, 3).")
        @ConfigEditorBoolean
        public boolean includeArrays;

        @Expose
        @ConfigOption(name = "Item Types", desc = "The list of items to display in the tracker.")
        @ConfigEditorDraggableList(
                exampleText = {
                        "Arrows",
                        "Snowballs",
                        "Eggs",
                        "Ender Pearls",
                }
        )
        public List<Integer> itemTypes;

        public ProjectileTracker(GUIElements.CoreSettings coreSettings, int prefixFormat, boolean includeArrays, List<Integer> itemTypes) {
            this.coreSettings = coreSettings;
            this.prefixFormat = prefixFormat;
            this.includeArrays = includeArrays;
            this.itemTypes = itemTypes;
        }
    }

    public static class FoodTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat;

        @Expose
        @ConfigOption(name = "Print Arrays", desc = "If items are contained in more than 1 slot, show the number in each slot in order (hotbar from left to right, then inventory rows 1, 2, 3).")
        @ConfigEditorBoolean
        public boolean includeArrays;

        @Expose
        @ConfigOption(name = "Item Types", desc = "The list of items to display in the tracker.")
        @ConfigEditorDraggableList(
                exampleText = {
                        "Apple",
                        "Baked Potato",
                        "Bread",
                        "Carrot",
                        "Cake",
                        "Cooked Fish",
                        "Cooked Mutton",
                        "Cooked Rabbit",
                        "Cooked Chicken",
                        "Cooked Porkchop",
                        "Cookie",
                        "Golden Apple",
                        "Golden Carrot",
                        "Melon",
                        "Mushroom Stew",
                        "Potato",
                        "Pumpkin Pie",
                        "Rabbit Stew",
                        "Raw Beef",
                        "Raw Chicken",
                        "Raw Mutton",
                        "Raw Porkchop",
                        "Raw Rabbit",
                        "Fish",
                        "Steak",
                }
        )
        public List<Integer> itemTypes;

        public FoodTracker(GUIElements.CoreSettings coreSettings, int prefixFormat, boolean includeArrays, List<Integer> itemTypes) {
            this.coreSettings = coreSettings;
            this.prefixFormat = prefixFormat;
            this.includeArrays = includeArrays;
            this.itemTypes = itemTypes;
        }
    }
}
