package io.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryTrackers {
    @Expose
    @ConfigOption(name = "Armour Info HUD", desc = "")
    @Accordion
    public EquipmentTracker equipmentTracker = new EquipmentTracker();

    @Expose
    @ConfigOption(name = "Projectile HUD", desc = "")
    @Accordion
    public ProjectileTracker projectileTracker = new ProjectileTracker();

    @Expose
    @ConfigOption(name = "Food HUD", desc = "")
    @Accordion
    public FoodTracker foodTracker = new FoodTracker();

    public static class EquipmentTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 150, 5, 3);

        @Expose
        @ConfigOption(name = "Prefix Format", desc = "Switch between displaying the prefix as the item's icon or name.")
        @ConfigEditorDropdown(values = {"Icon", "Name"})
        public int prefixFormat = 1;

        @Expose
        @ConfigOption(name = "Show Durability Bar", desc = "If \"Prefix Format\" is set to \"Icon\", display the durability bar of the item.")
        @ConfigEditorBoolean
        public boolean showDurabilityBar = true;

        @Expose
        @ConfigOption(name = "Durability Format", desc = "Switch between displaying the durability as a percentage, fraction or number (fraction without the denominator).")
        @ConfigEditorDropdown(values = {"Percentage", "Fraction", "Number"})
        public int durabilityFormat = 0;

        @Expose
        @ConfigOption(name = "Durability Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int durabilityDecimalPlaces = 1;

        @Expose
        @ConfigOption(name = "Item Types", desc = "The list of items to display in the tracker.")
        @ConfigEditorDraggableList(
                exampleText = {
                        "Hand",
                        "Helmet",
                        "Chestplate",
                        "Leggings",
                        "Boots",
                }
        )
        public List<Integer> itemTypes = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
    }

    public static class ProjectileTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 5, 3);

        @Expose
        @ConfigOption(name = "Show Arrows", desc = "")
        @ConfigEditorBoolean
        public boolean showArrows = true;

        @Expose
        @ConfigOption(name = "Show Snowballs", desc = "")
        @ConfigEditorBoolean
        public boolean showSnowballs = true;

        @Expose
        @ConfigOption(name = "Show Eggs", desc = "")
        @ConfigEditorBoolean
        public boolean showEggs = true;

        @Expose
        @ConfigOption(name = "Show Ender Pearls", desc = "")
        @ConfigEditorBoolean
        public boolean showEnderPearls = true;

        @Expose
        @ConfigOption(name = "Show Slot Counts", desc = "If items are contained in more than 1 slot, show the number in each slot in order (hotbar from left to right, then inventory rows 1, 2, 3).")
        @ConfigEditorBoolean
        public boolean showSlotCounts = false;
    }

    public static class FoodTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 70, 3);

        @Expose
        @ConfigOption(name = "Show Slot Counts", desc = "If items are contained in more than 1 slot, show the number in each slot in order (hotbar from left to right, then inventory rows 1, 2, 3).")
        @ConfigEditorBoolean
        public boolean showSlotCounts = false;
    }
}
