package net.sinender.common.action.actions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.ai.TargetSelector
import net.minestom.server.entity.ai.goal.MeleeAttackGoal
import net.minestom.server.entity.ai.target.ClosestEntityTarget
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.StringProperty
import net.sinender.common.managers.Sandbox
import net.sinender.utils.colorize
import java.time.Duration
import java.util.List


class ActionBar: Action(
    ActionEnum.ACTIONBAR,
    "Display Action Bar",
    "Sends a message through the action bar to the player.",
    Material.NAME_TAG
) {
    init {
        property(
            StringProperty(
                "message",
                "Message",
                "The message to send to the player.",
                "<yellow>Hello, world!"
            )
        )
    }

    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        player?.sendActionBar(colorize(getValue("message", String::class.java) ?: ""))
    }
}