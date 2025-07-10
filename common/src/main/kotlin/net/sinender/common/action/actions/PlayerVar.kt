package net.sinender.common.action.actions

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.StringPropertyAnnotation
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.feature.evalex.VariableFunctions
import net.sinender.common.feature.variables.getVar
import net.sinender.common.feature.variables.setVar
import net.sinender.common.postgres.Sandbox

data class PlayerVar(
    @DisplayName("Name") @Description("The name of the variable to modify.")
    @StringPropertyAnnotation
    val key: String = "Kills",
    @DisplayName("Value") @Description("The value to change the variable to.")
    @StringPropertyAnnotation
    val value: String = "1",
) : Action(
    ActionEnum.PLAYER_VAR,
    "Change Player Variable",
    "Modify the value of the player's variable.",
    Material.FEATHER
) {
    override suspend fun execute(
        entity: Entity,
        player: Player?,
        sandbox: Sandbox,
        event: Event?,
        expression: (String) -> PGExpression
    ) {
        player?.setVar(
            key,
            expression(value).withValues(
                mutableMapOf(
                    "this" to player.getVar(key),
                    "value" to player.getVar(key),
                    "val" to player.getVar(key)
                )
            )
        )
    }
}