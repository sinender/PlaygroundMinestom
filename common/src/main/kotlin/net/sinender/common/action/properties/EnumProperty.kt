package net.sinender.common.action.properties

import net.sinender.utils.PaginationList
import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.common.managers.Sandbox
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.PotionContents
import net.minestom.server.potion.PotionEffect
import net.minestom.server.potion.PotionType
import net.sinender.common.menu.menuItem
import net.sinender.utils.EnumMaterial

class EnumProperty (
    id: String,
    displayName: String,
    description: String?,
    material: Material,
    val enumClazz: Class<out Enum<*>>,
    value: Enum<*>? = null
): ActionProperty<Enum<*>>(id, displayName, description, material, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        // imo this doesn't need an entire class dedicated to it so object whatever it's called thingy it is!
        val menu = object: PaginationMenu("Select Option") {
            override fun paginationList(player: Player): PaginationList<MenuItem> {
                val menuItems = ArrayList<MenuItem>()
                for (item in enumClazz.enumConstants) {
                    item as? EnumMaterial ?: continue
                    val builder = menuItem(item.material?: Material.PAPER)
                    {
                        if (event.clickType === ClickType.LEFT_CLICK) value = item
                        actionEditMenu.open(player)
                    }
                    builder.name("<yellow>${item.name}")
                    builder.action(ClickType.LEFT_CLICK, "to add")
                    menuItems.add(builder)
                }
                return PaginationList(menuItems, slots.size)
            }
        }

        menu.open(player)
    }

    override fun fromValue(value: Any?) {
        this.value = java.lang.Enum.valueOf(enumClazz, value as String)
    }

    override fun toValue(): Any? {
        return value?.name
    }
}

fun <E : Enum<E>>List<ActionProperty<*>>.enum(id: String): Enum<E>? {
    return this.filter { it.id == id }
        .map { it.value }
        .firstOrNull() as? Enum<E>?
}