package com.github.brainage04.togglesprint.utils

import com.github.brainage04.togglesprint.Main.Companion.MOD_NAME
import com.github.brainage04.togglesprint.utils.SoundUtils.playAlertSound
import com.github.brainage04.togglesprint.utils.SoundUtils.playNotificationSound
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ChatUtils {
    private const val defaultPrefix = "§e[${MOD_NAME}]§r "
    private const val redPrefix = "§e[${MOD_NAME}]§c "
    private const val yellowPrefix = "§e[${MOD_NAME}]§e "
    private const val greenPrefix = "§e[${MOD_NAME}]§a "

    enum class PrefixType {
        DEFAULT,
        RED,
        YELLOW,
        GREEN
    }

    enum class SoundType {
        NONE,
        NOTIFICATION,
        ALERT
    }

    fun messageToChat(message: String, prefixType: PrefixType = PrefixType.DEFAULT, soundType: SoundType = SoundType.NONE) {
        var prefix = defaultPrefix

        when (prefixType) {
            PrefixType.RED -> prefix = redPrefix
            PrefixType.YELLOW -> prefix = yellowPrefix
            PrefixType.GREEN -> prefix = greenPrefix
            else -> {}
        }

        when (soundType) {
            SoundType.NOTIFICATION -> playNotificationSound()
            SoundType.ALERT -> playAlertSound()
            else -> {}
        }

        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(prefix + message))
    }
}