package feature.actionMenu

import action.Action
import managers.getSandbox
import menu.Backable
import menu.Menu
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType

class ActionEditMenu(
    val action: Action,
    val menu: Menu
): Menu(
    "Action Edit",
    if (action.properties.size > 9) InventoryType.CHEST_5_ROW else InventoryType.CHEST_4_ROW
), Backable {
    override fun setupItems(player: Player) {
        val sandbox = player.getSandbox() ?: return
        for ((index, property) in action.properties.withIndex()) {
            val item = property.getDisplayItem()
            item.action = {event -> property.runnable(event, sandbox, player, this)}

            addItem(slots[index], item)
        }
    }

    override fun back(player: Player) {
        menu.open(player)
    }

    override fun backName(player: Player): String {
        return menu.title
    }
}