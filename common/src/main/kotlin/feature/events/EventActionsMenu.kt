package feature.events

import PaginationList
import feature.actionMenu.ActionsMenu
import feature.housingMenu.PlaygroundMenu
import feature.housingMenu.SystemsMenu
import managers.getSandbox
import menu.*
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.Material
import java.util.HashMap

class EventActionsMenu : PaginationMenu(
    "Event Actions",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val sandbox = player.getSandbox() ?: return null
        val eventActions = sandbox.eventsList ?: HashMap();
        val menuItems = ArrayList<MenuItem>()
        for (event in EventType.entries) {
            if (!eventActions.contains(event.name)) {
                eventActions[event.name] = ArrayList()
            }
            val menuItem = event.menuItem.clone()
            menuItem.action = {
                ActionsMenu(eventActions[event.name]?: ArrayList(), this).open(player)
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