package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.utils.isInteger
import net.sinender.common.managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import net.sinender.utils.err
import net.sinender.utils.info
import net.sinender.utils.success
import net.sinender.utils.openChat

class DoubleProperty (
    id: String,
    displayName: String,
    description: String?,
    value: Double? = null,
    val min: Double = 0.0,
    val max: Double = Double.MAX_VALUE
) : ActionProperty<Double>(id, displayName, description, Material.PAPER, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        player.info("<green>Please type the value you want to set for $displayName:")
        player.openChat(this.value.toString() ?: "") { message ->
            if (!message.isInteger()) {
                player.err("The value provided is not a number.")
                return@openChat
            }

            val valueTemp = message.toDoubleOrNull()
            if (valueTemp != null && (valueTemp < min || valueTemp > max)) {
                player.err("The value must be between $min and $max")
                return@openChat
            }

            this.value = valueTemp
            player.success("Value set to $message")
            actionEditMenu.open(player)
        }
    }
}

fun List<ActionProperty<*>>.double(id: String): Double? {
    return this.filter { it.id == id }
        .map { it.value }
        .firstOrNull() as? Double
}