package feature.actionMenu

import PaginationList
import action.ActionEnum
import menu.Backable
import menu.MenuItem
import menu.PaginationMenu
import net.minestom.server.entity.Player

class AddActionMenu(
    private val backMenu: ActionsMenu
): PaginationMenu("Add Action"), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem> {
        val menuItems = ArrayList<MenuItem>()
        for (entry in ActionEnum.entries) {
            // why is this the only way i can find to create the item
            val action = entry.clazz.getDeclaredConstructor().newInstance()
            val item = action.createAddDisplayItem()
            item.action = {
                backMenu.actions.add(action)
                backMenu.open(player)
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