package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.DoublePropertyAnnotation
import net.sinender.common.postgres.Sandbox

data class WaitAction(
    @DisplayName("Duration") @Description("The duration to wait in ticks.")
    @DoublePropertyAnnotation
    val duration: Double = 5.0,
): Action(
    ActionEnum.WAIT,
    "Wait",
    "Pauses action execution for a specified amount of time.",
    Material.CLOCK
) {

    override suspend fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        // Convert duration from ticks to milliseconds
        val waitTime = (duration * 50).toLong() // 1 tick = 50 milliseconds
        kotlinx.coroutines.delay(waitTime)

        // After waiting, you can continue with any further actions if needed
        // For now, this action simply waits and does nothing else.
    }
}