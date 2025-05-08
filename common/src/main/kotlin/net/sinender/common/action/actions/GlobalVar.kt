package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.StringPropertyAnnotation
import net.sinender.common.feature.variables.setVar
import net.sinender.common.postgres.Sandbox

data class GlobalVar(
    @DisplayName("Name") @Description("The name of the variable to modify.")
    @StringPropertyAnnotation
    val key: String = "Kills",
    @DisplayName("Value") @Description("The value to change the variable to.")
    @StringPropertyAnnotation
    val value: String = "1",
) : Action(
    ActionEnum.GLOBAL_VAR,
    "Change Global Variable",
    "Modify the value of a global variable.",
    Material.FEATHER
) {
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        sandbox.setVar(key, value)
    }
}