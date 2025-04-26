package net.sinender.common.action.actions

import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.IntegerProperty
import net.sinender.common.action.properties.PotionProperty
import net.sinender.common.managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect

class PotionEffect: Action(
    ActionEnum.POTION_EFFECT,
    "Apply Potion Effect",
    "Gives the player a potion effect.",
    Material.POTION
) {
    init {
        property(
            PotionProperty(
                "potion",
                "Effect",
                "The potion effect to give to the player.",
                PotionEffect.GLOWING
            )
        )
        property(
            IntegerProperty(
                "amplifier",
                "Amplifier",
                "The amplifier (level) of the potion.",
                1,
                1,
                255
            )
        )
        property(
            IntegerProperty(
                "duration",
                "Duration",
                "How long the player has the effect. Use -1 to make the effect not expire.",
                60,
                -1,
                1000000
            )
        )
    }

    override fun execute(player: Player, sandbox: Sandbox) {
        val potion: Potion = Potion(
            getValue("potion", PotionEffect::class.java)!!,
            (getValue("amplifier", Int::class.java)!! - 1),
            (getValue("duration", Int::class.java)!! * 20))

        player.addEffect(potion)
    }
}