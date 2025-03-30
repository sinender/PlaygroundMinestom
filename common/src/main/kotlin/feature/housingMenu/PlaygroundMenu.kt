package feature.housingMenu

import menu.Menu
import menu.menuItem
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

class PlaygroundMenu: Menu(
    "Playground Menu",
    InventoryType.CHEST_6_ROW
){
    override fun setupItems(player: Player) {
        for (i in 0 until type.size) { //Template for adding items
            addItem(i, menuItem(Material.AIR) {
                player.sendMessage("You clicked on item $i")
            })
        }

        addItem(22, menuItem(Material.ACTIVATOR_RAIL) {

        }.name("<green>Systems Menu")
            .description("Contains all the systems for your Sandbox")
            .action(ClickType.LEFT_CLICK, "to edit"))
    }
}