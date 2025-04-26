package action.properties

import action.ActionProperty
import feature.actionMenu.ActionEditMenu
import isInteger
import managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import net.sinender.utils.err
import net.sinender.utils.info
import net.sinender.utils.success
import openChat

class IntegerProperty (
    id: String,
    displayName: String,
    description: String?,
    value: Int? = null,
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE
) : ActionProperty<Int>(id, displayName, description, Material.PAPER, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        player.info("<green>Please type the value you want to set for $displayName:")
        player.openChat(this.value.toString() ?: "") { message ->
            if (!message.isInteger()) {
                player.err("The value provided is not a number.")
                return@openChat
            }

            val valueTemp = message.toInt()
            if (valueTemp < min || valueTemp > max) {
                player.err("The value must be between $min and $max")
                return@openChat
            }

            this.value = valueTemp
            player.success("Value set to $message")
            actionEditMenu.open(player)
        }
    }
}