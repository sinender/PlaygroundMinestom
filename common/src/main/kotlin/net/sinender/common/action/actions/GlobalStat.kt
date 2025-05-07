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
import net.sinender.common.feature.stats.setStat
import net.sinender.common.postgres.Sandbox

data class GlobalStat(
    @DisplayName("Name") @Description("The name of the stat to modify.")
    @StringPropertyAnnotation
    val key: String = "Kills",
    @DisplayName("Value") @Description("The value to change the stat to.")
    @StringPropertyAnnotation
    val value: String = "1",
) : Action(
    ActionEnum.GLOBAL_STAT,
    "Change Global Stat",
    "Modify the value of a global stat.",
    Material.FEATHER
) {
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        sandbox.setStat(key, value)
    }
}