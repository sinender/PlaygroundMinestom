package net.sinender.common.feature.actionMenu

import net.sinender.utils.PaginationList
import net.sinender.common.action.ActionEnum
import feature.actionMenu.ActionsMenu
import net.sinender.common.menu.Backable
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
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