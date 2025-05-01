package net.sinender.common.commands

import net.sinender.lobby.LOBBY_INSTANCE
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.sinender.utils.setInstanceSafe
import net.sinender.utils.success
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class HubCommand: CommandInstance {
    @Command("hub")
    fun hub(actor: MinestomCommandActor) {
        actor.requirePlayer()
        actor.asPlayer()!!.setInstanceSafe(LOBBY_INSTANCE, Pos(0.0, 3.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.ADVENTURE
        actor.success("You have been teleported to the hub!")
    }
}