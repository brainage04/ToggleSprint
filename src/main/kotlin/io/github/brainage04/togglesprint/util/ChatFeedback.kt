package io.github.brainage04.togglesprint.util

import io.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

/**
 * Toggle Sprint's chat output. Each message starts with a grey `[Toggle Sprint]` prefix and has a
 * body colour for its kind: info white, success green, warning yellow, error red. The colour is only
 * the default: § codes inside a message keep their own colours, and after a `§r` the text returns to
 * the kind's colour. A list is sent as one prefixed header followed by unprefixed [detail] lines.
 */
object ChatFeedback {
    private const val PREFIX = "${ChatUtils.GRAY_CHAR}[${ToggleSprintMain.MOD_NAME}] "

    /** A query's result, or the header of a listed result. */
    fun info(message: String) = send(PREFIX + body(ChatUtils.WHITE_CHAR, message))

    /** A completed action. */
    fun success(message: String) = send(PREFIX + body(ChatUtils.GREEN_CHAR, message))

    /** Nothing to do, or advice. */
    fun warning(message: String) = send(PREFIX + body(ChatUtils.YELLOW_CHAR, message))

    /** Wrong input or usage. */
    fun error(message: String) = send(PREFIX + body(ChatUtils.RED_CHAR, message))

    /** A line below a header, without the prefix. */
    fun detail(message: String) = send(body(ChatUtils.WHITE_CHAR, message))

    private fun body(
        colour: String,
        message: String,
    ): String = colour + message.replace(ChatUtils.RESET_CHAR, ChatUtils.RESET_CHAR + colour)

    private fun send(message: String) {
        Minecraft.getMinecraft().thePlayer?.addChatMessage(ChatComponentText(message))
    }
}
