package net.sinender.common.action.actions

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.trait.CancellableEvent
import net.minestom.server.item.Material
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.postgres.Sandbox

class CancelEvent : Action(
    ActionEnum.CANCEL_EVENT,
    "Cancel Event",
    "Stops an event from executing.",
    Material.TNT
) {

    override fun syncExecute(
        entity: Entity, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression
    ): EvaluationValue? {
        if (event == null || event !is CancellableEvent) {
            return null
        }
        event.isCancelled = true

        return null
    }

    override suspend fun execute(
        entity: Entity,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        //Do nothing, because if its async then it will not be able to cancel the event
    }

    override fun requiresEvent(): Boolean {
        return true
    }
}