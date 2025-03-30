package menu

import net.minestom.server.entity.Player

interface Backable {
    fun back(player: Player)
    fun backName(player: Player): String
}