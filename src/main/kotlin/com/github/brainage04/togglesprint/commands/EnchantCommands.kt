package com.github.brainage04.togglesprint.commands

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.EnchantmentUtils
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler
import java.util.Locale

/** `/getenchants`, `/getenchantinfo` and `/blacklistedenchants`, ported from BrainageHUD. */
object EnchantCommands {
    private val blacklist: MutableList<String>
        get() = ConfigUtils.brainageHudParity.enchantInfo.blacklistedEnchantmentIds

    fun registerCommands() {
        register("getenchants", ::getEnchants) { args ->
            if (args.size == 1) CommandBase.getListOfStringsMatchingLastWord(args, Item.itemRegistry.keys) else emptyList()
        }
        register("getenchantinfo", ::getEnchantInfo) { args ->
            if (args.size == 1) CommandBase.getListOfStringsMatchingLastWord(args, Enchantment.func_181077_c()) else emptyList()
        }
        register("blacklistedenchants", ::blacklistedEnchants) { args ->
            when (args.size) {
                1 -> CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove", "query")
                2 -> when (args[0].lowercase(Locale.ROOT)) {
                    "add" -> CommandBase.getListOfStringsMatchingLastWord(args, EnchantmentUtils.all.map(EnchantmentUtils::id).filter { it !in blacklist })
                    "remove" -> CommandBase.getListOfStringsMatchingLastWord(args, blacklist)
                    else -> emptyList()
                }
                else -> emptyList()
            }
        }
    }

    private fun register(name: String, execute: (Array<String>) -> Unit, tabComplete: (Array<String>) -> List<String>) {
        ClientCommandHandler.instance.registerCommand(SimpleCommand(
            name,
            object : SimpleCommand.ProcessCommandRunnable() {
                override fun processCommand(sender: ICommandSender?, args: Array<String>?) = execute(args ?: emptyArray())
            },
            object : SimpleCommand.TabCompleteRunnable {
                override fun tabComplete(sender: ICommandSender?, args: Array<String>?, pos: BlockPos?) = tabComplete(args ?: emptyArray())
            },
        ))
    }

    private fun feedback(message: String) {
        Minecraft.getMinecraft().thePlayer?.addChatMessage(ChatComponentText(message))
    }

    private fun usage(message: String) = ChatUtils.messageToChat(message, ChatUtils.PrefixType.RED)

    private fun getEnchants(args: Array<String>) {
        val stack: ItemStack? = when (args.size) {
            0 -> Minecraft.getMinecraft().thePlayer?.heldItem
            1 -> {
                val item = Item.getByNameOrId(args[0])
                if (item == null) {
                    usage("Unknown item: ${args[0]}")
                    return
                }
                ItemStack(item)
            }
            else -> {
                usage("Usage: /getenchants [item]")
                return
            }
        }

        val acceptableEnchantments = if (stack == null) emptyList() else
            EnchantmentUtils.all.filter { !EnchantmentUtils.isBlacklisted(it) && it.canApply(stack) }
        if (stack == null || acceptableEnchantments.isEmpty()) {
            feedback("No acceptable enchantments found!")
            return
        }

        val (unconflicted, conflictGroups) = EnchantmentUtils.groupConflicting(acceptableEnchantments).partition { it.size == 1 }

        feedback("${ChatUtils.boldChar}Acceptable enchants for ${stack.displayName}${ChatUtils.resetChar + ChatUtils.boldChar}:")

        if (conflictGroups.isNotEmpty()) {
            // groups are linked by conflicts, not necessarily all mutually exclusive: fortune
            // excludes silk touch, but protection enchantments all accept feather falling
            feedback("Enchantments that conflict within each group:")
            for (group in conflictGroups) {
                feedback(" - " + group.joinToString(", ") { EnchantmentUtils.describeFor(it, stack) })
            }
        }

        if (unconflicted.isNotEmpty()) {
            feedback("Enchantments with no conflicts:")
            for (group in unconflicted) feedback(" - " + EnchantmentUtils.describeFor(group.single(), stack))
        }
    }

    private fun getEnchantInfo(args: Array<String>) {
        if (args.isEmpty()) {
            usage("Usage: /getenchantinfo <enchantment ID or name>")
            return
        }

        val query = args.joinToString(" ").trim('"')
        val byId = EnchantmentUtils.byId(query)
        if (byId != null) {
            sendEnchantmentInfo(byId)
            return
        }

        val matches = EnchantmentUtils.findMatches(query)
        when {
            matches.size == 1 -> {
                feedback("Exact match found - ${EnchantmentUtils.chatName(matches.single())}")
                sendEnchantmentInfo(matches.single())
            }
            matches.isEmpty() -> feedback("No potential matches found!")
            else -> {
                feedback("No exact match found. Potential matches:")
                for (enchantment in matches) {
                    feedback("${EnchantmentUtils.chatName(enchantment)} - ${EnchantmentUtils.id(enchantment)}")
                }
            }
        }
    }

    private fun sendEnchantmentInfo(enchantment: Enchantment) {
        val bold = ChatUtils.boldChar
        feedback("${bold}Enchant info for ${ChatUtils.grayChar}$bold${EnchantmentUtils.name(enchantment)}${ChatUtils.resetChar}$bold:")
        feedback("ID: ${EnchantmentUtils.id(enchantment)}")
        feedback("Max level: ${enchantment.maxLevel}")

        val incompatible = EnchantmentUtils.all.filter { it != enchantment && !EnchantmentUtils.areCompatible(enchantment, it) }
        feedback("Incompatible with: " + joinOrNotApplicable(incompatible.map(EnchantmentUtils::chatName)))

        val appliedTo = Item.itemRegistry.filter { enchantment.canApply(ItemStack(it)) }.map { ItemStack(it).displayName }.distinct()
        feedback("Applied to: " + joinOrNotApplicable(appliedTo))
    }

    private fun joinOrNotApplicable(names: List<String>) = if (names.isEmpty()) "N/A" else names.joinToString(", ")

    private fun blacklistedEnchants(args: Array<String>) {
        when (args.firstOrNull()?.lowercase(Locale.ROOT)) {
            "add" -> resolveArgument(args, "add")?.let(::addToBlacklist)
            "remove" -> resolveArgument(args, "remove")?.let(::removeFromBlacklist)
            "query" -> queryBlacklist()
            else -> usage("Usage: /blacklistedenchants <add|remove> <enchantment> or /blacklistedenchants query")
        }
    }

    /** The enchantment named by an ID or an exact name after the subcommand. */
    private fun resolveArgument(args: Array<String>, subcommand: String): Enchantment? {
        if (args.size < 2) {
            usage("Usage: /blacklistedenchants $subcommand <enchantment>")
            return null
        }

        val query = args.drop(1).joinToString(" ").trim('"')
        val enchantment = EnchantmentUtils.byId(query)
            ?: EnchantmentUtils.all.firstOrNull { EnchantmentUtils.name(it).equals(query, ignoreCase = true) }
        if (enchantment == null) usage("Unknown enchantment: $query")
        return enchantment
    }

    private fun addToBlacklist(enchantment: Enchantment) {
        val id = EnchantmentUtils.id(enchantment)
        if (id in blacklist) {
            feedback("${EnchantmentUtils.chatName(enchantment)} is already blacklisted!")
            return
        }

        blacklist.add(id)
        ToggleSprintMain.configManager.save()
        feedback("${EnchantmentUtils.chatName(enchantment)} is now blacklisted.")
    }

    private fun removeFromBlacklist(enchantment: Enchantment) {
        if (!blacklist.remove(EnchantmentUtils.id(enchantment))) {
            feedback("${EnchantmentUtils.chatName(enchantment)} is not blacklisted!")
            return
        }

        ToggleSprintMain.configManager.save()
        feedback("${EnchantmentUtils.chatName(enchantment)} is no longer blacklisted.")
    }

    private fun queryBlacklist() {
        if (blacklist.isEmpty()) {
            feedback("No blacklisted enchantments.")
            return
        }

        feedback("Enchantment blacklist:")
        for (id in blacklist) {
            // IDs of enchantments this game does not have are listed verbatim
            val enchantment = EnchantmentUtils.all.firstOrNull { EnchantmentUtils.id(it) == id }
            feedback(" - " + (enchantment?.let(EnchantmentUtils::chatName) ?: id))
        }
    }
}
