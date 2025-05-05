package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.utils.isInteger
import net.sinender.common.managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.sinender.common.menu.MenuItem
import net.sinender.utils.err
import net.sinender.utils.info
import net.sinender.utils.success
import net.sinender.utils.openChat

class BooleanProperty (
    id: String,
    displayName: String,
    description: String?,
    value: Boolean = false,
) : ActionProperty<Boolean>(id, displayName, description, Material.LIME_DYE, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        value = !value!!
    }

    override fun getDisplayItem(): MenuItem {
        return super.getDisplayItem()
            .itemStack(ItemStack.of(if (value == true) Material.LIME_DYE else Material.RED_DYE))
    }
}

fun List<ActionProperty<*>>.boolean(id: String): Boolean? {
    return this.filter { it.id == id }
        .map { it.value }
        .firstOrNull() as? Boolean
}