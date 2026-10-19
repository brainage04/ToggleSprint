package io.github.brainage04.togglesprint.keys

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.util.ChatFeedback
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.Item
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

/**
 * Lists every occupied slot in chat with its item, count, damage and NBT (enchantments, custom name
 * and so on). Unbound by default.
 */
class InventoryStatsKeybind : KeyBinding("Inventory Stats", Keyboard.KEY_NONE, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        val player = Minecraft.getMinecraft().thePlayer ?: return

        while (isPressed) printInventoryStats(player)
    }

    private fun printInventoryStats(player: EntityPlayerSP) {
        val inventory = player.inventory
        var foundItem = false
        for (slot in 0 until inventory.sizeInventory) {
            val stack = inventory.getStackInSlot(slot) ?: continue
            if (!foundItem) ChatFeedback.info("Inventory stats:")
            foundItem = true
            var line = "Slot $slot: ${stack.stackSize} × ${Item.itemRegistry.getNameForObject(stack.item)}"
            if (stack.itemDamage != 0) line += " {Damage:${stack.itemDamage}}"
            stack.tagCompound?.let { line += " $it" }
            ChatFeedback.detail(line)
        }
        if (!foundItem) ChatFeedback.warning("Your inventory is empty.")
    }
}
