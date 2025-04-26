package action.properties

import PaginationList
import action.ActionProperty
import feature.actionMenu.ActionEditMenu
import managers.Sandbox
import menu.MenuItem
import menu.PaginationMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.potion.PotionEffect

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
                    val builder = MenuItem(ItemStack.of(Material.POTION)) {
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