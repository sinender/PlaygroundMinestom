package menu

import PaginationList
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.Material

abstract class PaginationMenu(
    title: String,
    type: InventoryType = InventoryType.CHEST_6_ROW,
): Menu(title, type) {
    var page = 1
    override fun setupItems(player: Player) {
        clearItems(player)
        val paginationList = paginationList(player) ?: return

        val list = paginationList.getPage(page)?: run {
            addItem(center, notFoundItem())
            return
        }
        for (i in list.indices) {
            val item = list[i]
            addItem(slots[i], item)
        }

        if (page > 1) {
            addItem(type.size - 9, menuItem(Material.ARROW) {
                page--
                updateItems(player)
            }.name("<green>Previous Page"))
        }

        if (page < paginationList.getPageCount()) {
            addItem(type.size - 1, menuItem(Material.ARROW) {
                page++
                updateItems(player)
            }.name("<green>Next Page"))
        }
    }

    abstract fun paginationList(player: Player): PaginationList<MenuItem>?
    open fun notFoundItem(): MenuItem = menuItem(Material.BEDROCK) {
        it.player.sendMessage("No items found!")
    }.name("<red>No items found!")
}