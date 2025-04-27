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

class PotionProperty (
    id: String,
    displayName: String,
    description: String?,
    value: PotionEffect? = null
): ActionProperty<PotionEffect>(id, displayName, description, Material.POTION, value) {
    override fun runnable(event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, actionEditMenu: ActionEditMenu) {
        // imo this doesn't need an entire class dedicated to it so object whatever it's called thingy it is!
        val menu = object: PaginationMenu("Select Potion") {
            override fun paginationList(player: Player): PaginationList<MenuItem> {
                val menuItems = ArrayList<MenuItem>()
                for (effect in PotionEffect.values()) {
                    val builder = MenuItem(ItemStack.of(Material.POTION)
                        .with(ItemComponent.POTION_CONTENTS, PotionContents(PotionType.fromKey(effect.name())?: PotionType.WATER))
                    ) {
                        if (event.clickType === ClickType.LEFT_CLICK) value = effect
                        actionEditMenu.open(player)
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

    override fun fromValue(value: Any?) {
        this.value = PotionEffect.fromKey(value.toString())
    }

    override fun toValue(): Any? {
        return value?.name()
    }
}