package net.sinender.common.action.properties

import net.minestom.server.component.DataComponent
import net.minestom.server.component.DataComponents
import net.sinender.utils.PaginationList
import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.PotionContents
import net.minestom.server.potion.PotionEffect
import net.minestom.server.potion.PotionType
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import java.lang.reflect.Field

class PotionProperty: ActionProperty<PotionEffect>(
    PotionPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: InventoryPreClickEvent,
        sandbox: Sandbox,
        player: Player,
        menu: Menu
    ) {
        val menu = object: PaginationMenu("Select Potion") {
            override fun paginationList(player: Player): PaginationList<MenuItem> {
                val menuItems = ArrayList<MenuItem>()
                for (effect in PotionEffect.values()) {
                    val builder = MenuItem(ItemStack.of(Material.POTION)
                        .with(DataComponents.POTION_CONTENTS, PotionContents(PotionType.fromKey(effect.name())?: PotionType.WATER))
                    ) {
                        if (event.click is Left) value(obj, field, effect)
                        menu.open(player)
                    }
                    builder.name("<yellow>${effect.name()}")
                    builder.action(ClickType.LEFT_CLICK, "to add")
                    menuItems.add(builder)
                }
                return PaginationList(menuItems, slots.size)
            }
        }

        menu.open(player)
    }

    override fun displayValue(value: PotionEffect?): String? {
        if (value == null) {
            return "&cNone"
        }
        return value.name()
    }
}

@Target(AnnotationTarget.FIELD)
annotation class PotionPropertyAnnotation(
    val material: String = "POTION",
)