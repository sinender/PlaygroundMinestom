package net.sinender.common.action.actions

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.Material
import net.sinender.common.postgres.Sandbox

class CancelEvent : Action(
    ActionEnum.CANCEL_EVENT,
    "Cancel Event",
    "Stops an event from executing.",
    Material.TNT
) {

    override fun syncExecute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        if (event == null || event !is CancellableEvent) {
            return
        }
        event.isCancelled = true
    }

    override suspend fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        //Do nothing, because if its async then it will not be able to cancel the event
    }

    override fun requiresEvent(): Boolean {
        return true
    }
}