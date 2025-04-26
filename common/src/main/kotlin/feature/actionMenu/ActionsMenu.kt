package feature.actionMenu

import PaginationList
import action.Action
import menu.Backable
import menu.Menu
import menu.MenuItem
import menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType

class ActionsMenu(
    private val actions: MutableList<Action>,
    private val backMenu: Menu
): PaginationMenu(
    "Actions",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun setupItems(player: Player) {
        super.setupItems(player)
    }

    override fun paginationList(player: Player): PaginationList<MenuItem> {
        val menuItems = ArrayList<MenuItem>()
        for ((index, action) in actions.withIndex()) {
            val item = action.createDisplayItem()
            item.action = { event ->
                if (event.clickType == ClickType.LEFT_CLICK) ActionEditMenu(action, this).open(player)
                if (event.clickType == ClickType.RIGHT_CLICK) {
                    actions.removeAt(index)
                    open(player)
                }
            }

            menuItems.add(item)
        }

        return PaginationList(menuItems, slots.size)
    }

    override fun back(player: Player) {
        backMenu.open(player)
    }

    override fun backName(player: Player): String {
        return backMenu.title
    }
}