package net.sinender.common.menu.items

import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.sinender.common.menu.Menu
import net.sinender.common.menu.MenuItem

class BackItem(private val backMenu: Menu): MenuItem(
    ItemStack.of(Material.ARROW), {
        backMenu.open(it.player)
    }
) {
    init {
        name("<red>Go back")
        description("Go back to ${backMenu.title}")
        action(ClickType.LEFT_CLICK, "to go back")
    }
}