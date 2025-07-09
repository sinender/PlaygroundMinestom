package net.sinender.common.managers

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.component.DataComponents
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.PlayerPickBlockEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.ItemBlockState
import net.sinender.utils.err


fun handlePickBlock(event: PlayerPickBlockEvent) {
    val player = event.player
    if (player.gameMode != GameMode.CREATIVE) return  // Sanity

    // First try to get the block from the item registry
    val block = event.block
    var itemStack = ItemStack.of(Material.fromKey(block.key())?:
        return run {
            event.player.err(
                "Failed to pick block: ${block.key()} is not a valid block type."
            )
        }
    )

    // Still no item, nothing to do
    if (itemStack == null) return
    val inventory = player.inventory

    // If the item is already on the hotbar, swap to it
    for (i in 0..8) {
        if (!inventory.getItemStack(i).isSimilar(itemStack)) continue
        player.setHeldItemSlot(i.toByte())
        break
    }

    var targetSlot = player.heldSlot.toInt()
    val targetItem = inventory.getItemStack(targetSlot)
    if (targetItem.isSimilar(itemStack)) return
    if (!targetItem.isAir) {
        // Try to find an empty slot
        for (i in 0..8) {
            if (inventory.getItemStack(i).isAir) {
                targetSlot = i
                break
            }
        }
        // If we didnt find an empty slot its fine we can keep the original and replace.
    }

    // If the item already exists in the inventory, swap to it
    var existingSlot = -1
    for (i in 9..<inventory.size) {
        if (inventory.getItemStack(i).isSimilar(itemStack)) {
            existingSlot = i
            break
        }
    }

    if (existingSlot != -1) {
        val existingItem = inventory.getItemStack(existingSlot)
        inventory.setItemStack(existingSlot, itemStack)
        inventory.setItemStack(targetSlot, existingItem)
    } else {
        inventory.setItemStack(targetSlot, itemStack)
        if (targetSlot != player.heldSlot.toInt()) {
            player.setHeldItemSlot(targetSlot.toByte())
        }
    }
}