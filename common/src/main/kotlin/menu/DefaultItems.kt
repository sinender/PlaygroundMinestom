package menu

import net.minestom.server.item.Material

object DefaultItems {
    val FILLER = menuItem(Material.SHULKER_SHELL) {}
        .name("")
        .itemModel("playground:menu/filler")
}