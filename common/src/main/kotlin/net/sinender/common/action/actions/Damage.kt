package net.sinender.common.action.actions

import io.github.togar2.pvp.MinestomPvP
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.Damage
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.IntegerProperty
import net.sinender.common.managers.Sandbox

class DamageAction: Action(
    ActionEnum.DAMAGE,
    "Damage",
    "Damages the entity.",
    Material.DIAMOND_SWORD
) {
    init {
        properties.add(
            IntegerProperty(
                "damage",
                "Damage",
                "The amount of damage to deal to the entity.",
                1,
            )
        )
    }
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        if (entity is LivingEntity) {
            entity.damage(Damage.fromPlayer(player ?: return,
                (properties[0].value as Int).toFloat()
            ))
        }
    }
}