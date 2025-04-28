package net.sinender.common.feature.npc.menu

import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.feature.npc.events.NpcEventActionsMenu
import net.sinender.common.managers.Sandbox
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem

class NpcEditMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
) : Menu(
    "Edit NPC: ${npc.name}",
    InventoryType.CHEST_6_ROW
) {
    override fun setupItems(player: Player) {
        addItem(center - 9, npc.distinctMenuItem())
        addItem(center + 9 - 2,
            menuItem(Material.GRASS_BLOCK) {
            NpcEventActionsMenu(npc, sandbox).open(player)
        }.name("<green>Event Actions")
            .description("Click to edit npc event actions")
            .action(ClickType.LEFT_CLICK, "to edit")
        )
    }
}