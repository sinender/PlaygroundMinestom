package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.DoubleProperty
import net.sinender.common.action.properties.double
import net.sinender.common.managers.Sandbox

class WaitAction: Action(
    ActionEnum.WAIT,
    "Wait",
    "Waits for a specified amount of time.",
    Material.CLOCK
) {
    init {
        properties.add(
            DoubleProperty(
                "duration",
                "Duration",
                "The duration to wait in ticks.",
                5.0,
            )
        )
    }

    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        val duration = properties.double("duration") ?: return
        Thread.sleep((duration * 50).toLong()) // Convert ticks to milliseconds
    }

    override fun requiresEvent(): Boolean {
        return false
    }
}