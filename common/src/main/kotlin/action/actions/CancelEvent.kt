package action.actions

import action.Action
import action.ActionEnum
import managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.Material

class CancelEvent: Action(
    ActionEnum.CANCEL_EVENT,
    "Cancel Event",
    "Stops an event from executing.",
    Material.TNT
) {
    override fun execute(e: Event, player: Player, sandbox: Sandbox) {
        val cancellableEvent: CancellableEvent = e as CancellableEvent
        cancellableEvent.isCancelled = true
    }

    override fun requiresEvent(): Boolean {
        return true
    }
}