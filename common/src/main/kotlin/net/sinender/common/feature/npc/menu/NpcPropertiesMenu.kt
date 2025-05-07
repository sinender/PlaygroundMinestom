package net.sinender.common.feature.npc.menu

import net.minestom.server.entity.Player
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.menu.Backable
import net.sinender.common.menu.Menu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.PaginationMenu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.PaginationList

class NpcPropertiesMenu(
    private val npc: NpcEntity,
    private val sandbox: Sandbox
): PaginationMenu("Properties"), Backable {
    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        return PaginationList(
            npc.properties.map {
                val item = it.second.getDisplayItem(npc, it.first)
                item.action = { event -> it.second.runnable(it.first, npc, event, sandbox, player, this) }
                item
            },
            slots.size
        )
    }

    override fun back(player: Player) {
        NpcEditMenu(
            npc,
            sandbox
        ).open(player)
    }

    override fun backName(player: Player): String {
        return "Edit NPC: ${npc.name}"
    }
}