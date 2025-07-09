package net.sinender.common.action

import com.github.shynixn.mccoroutine.minestom.launch
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.sinender.common.action.actions.WaitAction
import net.sinender.common.postgres.Sandbox
import net.sinender.common.utils.minecraftServer

class ActionExecutor(
    private val entity: Entity, private val player: Player?, private val sandbox: Sandbox, private val event: Event?,
    private val actions: List<Action>
) {
    fun execute() {
        var syncActions = actions.takeWhile { it !is WaitAction }
        var asyncActions = actions.drop(syncActions.size)
        for (action in syncActions) {
            try {
                action.syncExecute(entity, player, sandbox, event)
            } catch (e: Exception) {
                MinecraftServer.LOGGER.error("Error executing action: ${action.name}", e)
            }
        }
        minecraftServer.launch {
            for (action in asyncActions) {
                try {
                    action.execute(entity, player, sandbox, event)
                } catch (e: Exception) {
                    MinecraftServer.LOGGER.error("Error executing action: ${action.name}", e)
                }
            }
        }

    }
}