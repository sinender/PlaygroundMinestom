package net.sinender.common.feature.housingMenu

import net.sinender.common.feature.events.EventActionsMenu
import net.sinender.common.menu.Backable
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

class SystemsMenu : Menu(
    "Systems",
    InventoryType.CHEST_6_ROW
), Backable {
    override fun setupItems(player: Player) {
        for (i in 0 until type.size) { //Template for adding items
            addItem(i, menuItem(Material.AIR) {
                player.sendMessage("You clicked on item $i")
            })
        }

        addItem(
            10, menuItem(Material.COBWEB) {
                EventActionsMenu().open(player)
            }.name("<green>Event Actions")
                .description("")
                .action(ClickType.LEFT_CLICK, "to edit")
        )
    }

    override fun back(player: Player) {
        PlaygroundMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Playground Menu"
    }
}