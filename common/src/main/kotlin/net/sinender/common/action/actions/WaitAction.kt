package net.sinender.common.action.actions

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.DoublePropertyAnnotation
import net.sinender.common.action.properties.NumberPropertyAnnotation
import net.sinender.common.action.properties.doubleValue
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.postgres.Sandbox

data class WaitAction(
    @DisplayName("Duration") @Description("The duration to wait in ticks.")
    @NumberPropertyAnnotation
    val duration: String = "5.0",
): Action(
    ActionEnum.WAIT,
    "Wait",
    "Pauses action execution for a specified amount of time.",
    Material.CLOCK
) {

    override suspend fun execute(
        entity: Entity,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        // Convert duration from ticks to milliseconds
        val duration = this.doubleValue(expression(duration))
        val waitTime = (duration * 50).toLong() // 1 tick = 50 milliseconds
        kotlinx.coroutines.delay(waitTime)

        // After waiting, you can continue with any further actions if needed
        // For now, this action simply waits and does nothing else.
    }
}