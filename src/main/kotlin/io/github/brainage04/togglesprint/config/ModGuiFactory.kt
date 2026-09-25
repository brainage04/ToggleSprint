package io.github.brainage04.togglesprint.config

import io.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

/** Gives the mod a "Config" button in Forge's Mods list, opening the MoulConfig editor. */
class ModGuiFactory : IModGuiFactory {
    override fun initialize(minecraftInstance: Minecraft) {}

    override fun mainConfigGuiClass(): Class<out GuiScreen> = ConfigGuiScreen::class.java

    override fun runtimeGuiCategories(): Set<IModGuiFactory.RuntimeOptionCategoryElement>? = null

    override fun getHandlerFor(element: IModGuiFactory.RuntimeOptionCategoryElement): IModGuiFactory.RuntimeOptionGuiHandler? = null

    /** Forge instantiates this with the Mods list as [parent]; it hands straight over to the MoulConfig editor. */
    class ConfigGuiScreen(@Suppress("unused") private val parent: GuiScreen) : GuiScreen() {
        override fun initGui() {
            mc.displayGuiScreen(ToggleSprintMain.configManager.configScreen())
        }
    }
}
