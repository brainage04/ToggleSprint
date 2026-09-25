package io.github.brainage04.togglesprint.command

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.command.SimpleCommand.ProcessCommandRunnable
import io.github.brainage04.togglesprint.util.ChatFeedback
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.ClientCommandHandler

class CommandManager {

    init {
        registerCommand("togglesprint") { args ->
            if (args.isEmpty()) {
                ToggleSprintMain.configManager.openConfigGui()
                return@registerCommand
            }

            if (args.size == 1 && args[0].equals("gui", ignoreCase = true)) {
                ToggleSprintMain.configManager.openElementEditor()
                return@registerCommand
            }

            ChatFeedback.error("Usage: /togglesprint [gui]")
        }
        registerCommand("fullbright", ::setFullbright)
    }

    private fun setFullbright(args: Array<String>) {
        val amount = args.singleOrNull()?.toFloatOrNull()
        if (amount == null || amount.isNaN() || amount.isInfinite() || amount < -1.0f || amount > 1.0f) {
            ChatFeedback.error("Usage: /fullbright <amount from -1 to 1>")
            return
        }

        ToggleSprintMain.config.brainageHudParity.fullbright = amount
        ToggleSprintMain.configManager.save()
        ChatFeedback.success("Fullbright set to $amount.")
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