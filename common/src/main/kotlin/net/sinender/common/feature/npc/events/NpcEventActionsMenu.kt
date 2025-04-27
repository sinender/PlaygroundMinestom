package net.sinender.common.feature.npc.events

import feature.actionMenu.ActionsMenu
import net.sinender.common.feature.housingMenu.SystemsMenu
import net.sinender.common.managers.getSandbox
import net.sinender.common.menu.*
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.sinender.utils.PaginationList
import java.util.HashMap

class NpcEventActionsMenu : PaginationMenu(
    "Npc Event Actions",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val sandbox = player.getSandbox() ?: return null
        val eventActions = sandbox.eventsList ?: HashMap();
        val menuItems = ArrayList<MenuItem>()
        for (event in NpcEventType.entries) {
            if (!eventActions.contains(event.name)) {
                eventActions[event.name] = ArrayList()
            }
            val menuItem = event.menuItem.clone()
            menuItem.action = {
                ActionsMenu(eventActions[event.name] ?: ArrayList(), this).open(player)
            }
            menuItems.add(menuItem)
        }

        return PaginationList(menuItems, slots.size)
    }

    override fun back(player: Player) {
        SystemsMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Systems Menu"
    }
}