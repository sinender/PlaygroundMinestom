package feature.housingMenu

import PaginationList
import managers.getSandbox
import menu.Backable
import menu.Menu
import menu.menuItem
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

class EventActionsMenu : Menu(
    "Systems",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun setupItems(player: Player) {
        for (i in 0 until type.size) { //Template for adding items
            addItem(i, menuItem(Material.AIR) {
                player.sendMessage("You clicked on item $i")
            })
        }

        var sandbox = player.getSandbox() ?: return
        sandbox.events
        var paginationList = PaginationList()
    }

    override fun back(player: Player) {
        PlaygroundMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Playground Menu"
    }
}