package com.github.brainage04.togglesprint.config;

import com.github.brainage04.togglesprint.ToggleSprintMain;
import com.github.brainage04.togglesprint.config.categories.GUIElements;
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys;
import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;

public class ToggleSprintConfig extends Config {

    @Override
    public String getTitle() {
        return ToggleSprintMain.MOD_NAME + " " + ToggleSprintMain.getVersion() + " by §cbrainage04§r, config by §5Moulberry §rand §5nea89";
    }

    @Override
    public void saveNow() {
        ToggleSprintMain.configManager.save();
    }

    @Expose
    @Category(name = "Toggle Sprint/Sneak", desc = "Edit basic toggle sprint/sneak settings.")
    public ToggleMovementKeys toggleMovementKeys = new ToggleMovementKeys();

    @Expose
    @Category(name = "GUI Elements", desc = "Edit toggle sprint/sneak and other GUI elements.")
    public GUIElements guiElements = new GUIElements();
}
