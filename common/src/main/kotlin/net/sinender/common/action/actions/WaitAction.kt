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
    "Waits for a specified amount of time.",
    Material.CLOCK
) {

    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        Thread.sleep((duration * 50).toLong()) // Convert ticks to milliseconds
    }
}