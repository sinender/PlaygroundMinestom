package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.common.managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.utils.info
import net.sinender.utils.isInteger
import net.sinender.utils.success
import net.sinender.utils.openChat

class StringProperty (
    id: String,
    displayName: String,
    description: String?,
    value: String? = null,
) : ActionProperty<String>(id, displayName, description, Material.PAPER, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        player.info("<green>Please type the value you want to set for $displayName:")
        player.openChat(this.value?:"") { message ->
            this.value = message
            player.success("Value set to $message")
            actionEditMenu.open(player)
        }
    }
}

fun List<ActionProperty<*>>.string(id: String): String? {
    return this.filter { it.id == id }
        .map { it.value }
        .firstOrNull() as? String
}