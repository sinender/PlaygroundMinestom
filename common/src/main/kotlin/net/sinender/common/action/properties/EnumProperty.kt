package net.sinender.common.action.properties

import net.sinender.utils.PaginationList
import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.EnumMaterial
import java.lang.reflect.Field

class EnumProperty: ActionProperty<Enum<*>>(
    EnumPropertyAnnotation::class
) {
    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        // imo this doesn't need an entire class dedicated to it so object whatever it's called thingy it is!
        val menu = object: PaginationMenu("Select Option") {
            override fun paginationList(player: Player): PaginationList<MenuItem> {
                val menuItems = ArrayList<MenuItem>()
                for (item in field.type.enumConstants) {
                    if (item !is Enum<*>) continue
                    item as? EnumMaterial ?: continue
                    val builder = menuItem(item.material?: Material.PAPER)
                    {
                        if (event.clickType === ClickType.LEFT_CLICK) value(obj, field, item)
                        menu.open(player)
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
}

@Target(AnnotationTarget.FIELD)
annotation class EnumPropertyAnnotation(
    val material: String,
)