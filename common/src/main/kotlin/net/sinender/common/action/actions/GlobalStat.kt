package net.sinender.common.action.actions

import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.StringProperty
import net.sinender.common.feature.stats.setStat
import net.sinender.common.managers.Sandbox

class GlobalStat: Action(
    ActionEnum.GLOBAL_STAT,
    "Change Global Stat",
    "Modify the value of a global stat.",
    Material.FEATHER
) {
    init {
        property(
            StringProperty(
                "key",
                "Name",
                "The name of the stat to modify.",
                "Kills"
            )
        )
        property(
            StringProperty(
                "value",
                "Value",
                "The value to change the stat to.",
                "1"
            )
        )
    }

    override fun execute(player: Player, sandbox: Sandbox) {
        sandbox.setStat(getValue("key", String::class.java) ?: "", getValue("value", String::class.java) ?: "")
    }
}