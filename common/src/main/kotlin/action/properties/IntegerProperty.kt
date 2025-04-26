package action.properties

import action.ActionProperty
import feature.actionMenu.ActionEditMenu
import managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import net.sinender.utils.info
import net.sinender.utils.success
import openChat

class IntegerProperty (
    id: String,
    displayName: String,
    description: String?,
    value: Int? = null
) : ActionProperty<Int>(id, displayName, description, Material.PAPER, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        player.info("<green>Please type the value you want to set for $displayName:")
        player.openChat(this.value.toString() ?: "") { message ->
            this.value = message.toInt()
            player.success("Value set to ${message}")
            actionEditMenu.open(player)
        }
    }
}