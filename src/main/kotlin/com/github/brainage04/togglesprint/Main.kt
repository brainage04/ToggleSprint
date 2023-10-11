package com.github.brainage04.togglesprint

import com.github.brainage04.togglesprint.commands.CommandManager
import com.github.brainage04.togglesprint.config.manager.ConfigManager
import com.github.brainage04.togglesprint.config.ToggleSprintConfig
import com.github.brainage04.togglesprint.features.ToggleMovementKeyHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager

@Mod(modid = Main.MOD_ID, useMetadata = true)
class Main {
    private fun registerEvents(vararg events: Any?) {
        for (event in events) {
            MinecraftForge.EVENT_BUS.register(event)
        }
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        CommandManager()

        registerEvents(
            ToggleMovementKeyHandler()
        )
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        configManager = ConfigManager()
        MinecraftForge.EVENT_BUS.register(configManager)
    }

    companion object {
        lateinit var configManager: ConfigManager
        const val MOD_ID = "togglesprint"
        const val MOD_NAME = "Toggle Sprint"

        val LOGGER = LogManager.getLogger(MOD_ID)

        @JvmStatic
        val version: String
            get() = Loader.instance().indexedModList[MOD_ID]!!.version

        val config: ToggleSprintConfig
            get() = configManager.config ?: error("config is null")
    }
}
