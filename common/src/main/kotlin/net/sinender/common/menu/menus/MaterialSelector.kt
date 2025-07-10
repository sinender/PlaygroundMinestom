package net.sinender.common.menu.menus

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.menu.Menu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
import net.sinender.common.menu.menuItem
import net.sinender.utils.PaginationList

class MaterialSelector(backMenu: Menu, val invoke: (Material) -> Unit): PaginationMenu(
    "Material Selector",
    InventoryType.CHEST_6_ROW
) {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        return PaginationList(
            Material.values().map { material ->
                menuItem(material) {
                    invoke(material)
                    player.sendMessage("Selected material: ${material.name()}")
                }.name("<green>${material.name()}")
                    .action(ClickType.LEFT_CLICK, "to select")
            },
            slots.size
        )
    }

}