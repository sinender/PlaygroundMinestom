package net.sinender.common.action.actions

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.AnyPropertyAnnotation
import net.sinender.common.action.properties.anyValue
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.postgres.Sandbox

class Return(
    @DisplayName("Return Value")
    @AnyPropertyAnnotation
    val returnValue: String? = null
) : Action(
    ActionEnum.FUNCTION_RETURN,
    "Return",
    "Returns from a function, or from the action chain if not in a function. Does not support return value if not in a function.",
    Material.ARROW
) {
    override fun syncExecute(
        entity: Entity, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression
    ): EvaluationValue? {
        if (returnValue == null) {
            return EvaluationValue.NULL_VALUE
        }

        return anyValue(expression(returnValue))
    }

    override suspend fun execute(
        entity: Entity,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {

    }
}