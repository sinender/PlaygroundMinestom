package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.damage.Damage
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.DoublePropertyAnnotation
import net.sinender.common.postgres.Sandbox

data class DamageAction(
    @DisplayName("Damage") @Description("The amount of damage to deal to the entity.")
    @DoublePropertyAnnotation
    val damage: Double = 1.0,
): Action(
    ActionEnum.DAMAGE,
    "Damage",
    "Damages the entity.",
    Material.DIAMOND_SWORD
) {
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        if (entity is LivingEntity) {
            entity.damage(Damage.fromPlayer(player ?: return,
                damage.toFloat()
            ))
        }
    }
}