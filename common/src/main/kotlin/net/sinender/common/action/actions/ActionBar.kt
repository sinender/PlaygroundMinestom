package net.sinender.common.action.actions

import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.StringProperty
import net.sinender.common.managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.sinender.utils.colorize

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

    override fun execute(player: Player, sandbox: Sandbox) {
        player.sendActionBar(colorize(getValue("message", String::class.java) ?: ""))
    }
}