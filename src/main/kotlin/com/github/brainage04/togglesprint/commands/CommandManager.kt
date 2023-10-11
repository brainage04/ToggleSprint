package com.github.brainage04.togglesprint.commands

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.commands.SimpleCommand.ProcessCommandRunnable
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.ClientCommandHandler

class CommandManager {

    init {
        registerCommand("togglesprint") { args ->
            if (args.isEmpty()) {
                ToggleSprintMain.configManager.openConfigGui()
                return@registerCommand
            }

            if (args.size == 1) {
                when (args[0]) {
                    "gui" -> {
                        // open the gui element editor here
                    }
                }
            }
        }
    }

    private fun registerCommand(name: String, function: (Array<String>) -> Unit) {
        ClientCommandHandler.instance.registerCommand(SimpleCommand(name, createCommand(function)))
    }

/*
    private fun registerCommand0(
        name: String,
        function: (Array<String>) -> Unit,
        autoComplete: ((Array<String>) -> List<String>) = { listOf() }
    ) {
        val command = SimpleCommand(
            name,
            createCommand(function),
            object : SimpleCommand.TabCompleteRunnable {
                override fun tabComplete(sender: ICommandSender?, args: Array<String>?, pos: BlockPos?): List<String> {
                    return autoComplete(args ?: emptyArray())
                }
            }
        )
        ClientCommandHandler.instance.registerCommand(command)
    }
 */

    private fun createCommand(function: (Array<String>) -> Unit) = object : ProcessCommandRunnable() {
        override fun processCommand(sender: ICommandSender?, args: Array<String>?) {
            if (args != null) function(args.asList().toTypedArray())
        }
    }
}