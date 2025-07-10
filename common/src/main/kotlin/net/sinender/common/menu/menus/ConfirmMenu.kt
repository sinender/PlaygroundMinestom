package net.sinender.common.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem

class ConfirmMenu(private val backMenu: Menu, val onConfirm: () -> Unit): Menu(
    "Confirm Action",
    type = InventoryType.CHEST_3_ROW
) {
    override fun setupItems(player: Player) {
        addItem(12, menuItem(Material.GREEN_WOOL) {
            onConfirm()
            player.sendMessage("<green>Action confirmed!")
            backMenu.open(player)
        }.name("<green>Confirm").action(ClickType.LEFT_CLICK, "to confirm"))

        addItem(14, menuItem(Material.RED_WOOL) {
            player.sendMessage("<red>Action cancelled.")
            backMenu.open(player)
        }.name("<red>Cancel").action(ClickType.LEFT_CLICK, "to cancel"))
    }
}