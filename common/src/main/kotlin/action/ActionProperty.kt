package action

import feature.actionMenu.ActionEditMenu
import managers.Sandbox
import menu.MenuItem
import menu.menuItem
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import org.bson.Document

abstract class ActionProperty<V>(
    val id: String,
    val displayName: String,
    private val description: String?,
    val material: Material,
    var value: V? = null
) {
    var rClick: String? = null
    var mClick: String? = null

    val item: MenuItem = menuItem(material) {

    }.name("<yellow>$displayName")
    .description(description)
    .leftClick("to edit")

    open fun displayValue(): String? {
        if (value == null) {
            return "&cNone"
        }
        return value.toString()
    }

    open fun getDisplayItem(): MenuItem {
        val item = item.clone()
        if (value != null) {
            item.info("<yellow>Current value", "")
                .info(null, "<green>" + displayValue())
        }
        return item
    }

    abstract fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu)

    open fun toValue(): Any? {
        return value
    }

    open fun fromValue(value: Any?) {
        this.value = value as? V
    }
}