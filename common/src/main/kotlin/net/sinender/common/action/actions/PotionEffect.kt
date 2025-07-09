package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.IntegerPropertyAnnotation
import net.sinender.common.action.properties.PotionPropertyAnnotation
import net.sinender.common.postgres.Sandbox

data class PotionEffect(
    @DisplayName("Potion Effect") @Description("The potion effect to apply.")
    @PotionPropertyAnnotation
    val effect: PotionEffect = PotionEffect.GLOWING,
    @DisplayName("Amplifier") @Description("The amplifier (level) of the potion.")
    @IntegerPropertyAnnotation(
        min = 0,
        max = 255
    )
    val amplifier: Int = 1,
    @DisplayName("Duration") @Description("The duration of the potion effect in seconds.")
    @IntegerPropertyAnnotation(
        min = 1,
        max = 1000000
    )
    val duration: Int = 30
) : Action(
    ActionEnum.POTION_EFFECT,
    "Apply Potion Effect",
    "Gives the player a potion effect.",
    Material.POTION
) {
    override suspend fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        val potion = Potion(
            effect,
            (amplifier - 1),
            (duration * 20)
        )

        player?.addEffect(potion)
    }
}