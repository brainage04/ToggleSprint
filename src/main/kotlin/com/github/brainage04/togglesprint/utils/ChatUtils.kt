package com.github.brainage04.togglesprint.utils

import com.github.brainage04.togglesprint.ToggleSprintMain.Companion.MOD_NAME
import com.github.brainage04.togglesprint.utils.SoundUtils.playAlertSound
import com.github.brainage04.togglesprint.utils.SoundUtils.playNotificationSound
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object ChatUtils {
    const val darkRedChar = "§4"
    const val redChar = "§c"
    const val goldChar = "§6"
    const val yellowChar = "§e"
    const val darkGreenChar = "§2"
    const val greenChar = "§a"
    const val aquaChar = "§b"
    const val darkAquaChar = "§3"
    const val darkBlueChar = "§1"
    const val blueChar = "§9"
    const val lightPurpleChar = "§d"
    const val darkPurpleChar = "§5"
    const val whiteChar = "§f"
    const val grayChar = "§7"
    const val darkGrayChar = "§8"
    const val blackChar = "§0"

    val colourChars = arrayListOf(
        darkRedChar,
        redChar,
        goldChar,
        yellowChar,
        darkGreenChar,
        greenChar,
        aquaChar,
        darkAquaChar,
        darkBlueChar,
        blueChar,
        lightPurpleChar,
        darkPurpleChar,
        whiteChar,
        grayChar,
        darkGrayChar,
        blackChar,
    )

    const val obfuscatedChar = "§k"
    const val boldChar = "§l"
    const val strikethroughChar = "§m"
    const val underlineChar = "§n"
    const val italicChar = "§o"

    val effectChars = arrayListOf(
        "",
        obfuscatedChar,
        boldChar,
        strikethroughChar,
        underlineChar,
        italicChar,
    )

    const val resetChar = "§r"

    const val defaultPrefix = "$yellowChar[${MOD_NAME}]${resetChar + whiteChar} "
    const val redPrefix = "$yellowChar[${MOD_NAME}]$redChar "
    const val yellowPrefix = "$yellowChar[${MOD_NAME}]$yellowChar "
    const val greenPrefix = "$yellowChar[${MOD_NAME}]$greenChar "

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