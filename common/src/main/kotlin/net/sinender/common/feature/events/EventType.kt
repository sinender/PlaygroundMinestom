package net.sinender.common.feature.events

import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.menuItem
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

enum class EventType(
    val clazz: Class<out PlayerEvent>,
    val menuItem: MenuItem,
) {
    BLOCK_BREAK(PlayerBlockBreakEvent::class.java,
        createMenuItem(
            Material.DIAMOND_PICKAXE,
            "Block Break",
            "a block is broken by a player"
        )
    ),
    INTERACT_PLAYER(PlayerEntityInteractEvent::class.java,
        createMenuItem(
            Material.RABBIT_STEW,
            "Player Interact",
            "a player interacts with another player"
        )
    ),

    ;
}

fun createMenuItem(material: Material, name: String, description: String = "<red>No description set!"): MenuItem {
    return menuItem(
        material
    ) {

    }.name("<green>${name}")
     .description("Execute actions when $description")
     .action(ClickType.LEFT_CLICK, "to edit")
}