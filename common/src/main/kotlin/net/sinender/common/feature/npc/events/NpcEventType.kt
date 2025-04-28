package net.sinender.common.feature.npc.events

import net.minestom.server.event.entity.EntityDamageEvent
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.menuItem
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.trait.EntityEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material

enum class NpcEventType(
    val clazz: Class<out EntityEvent>,
    val menuItem: MenuItem,
) {
    INTERACT(PlayerEntityInteractEvent::class.java, createMenuItem(Material.BONE, "Interact", "a player has interacted (right clicked) with the NPC")),
    ATTACK(EntityDamageEvent::class.java, createMenuItem(Material.DIAMOND_SWORD, "Attack", "an NPC has taken damage. This includes being attacked by a player or another entity")),

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