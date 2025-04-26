package action.actions

import action.Action
import action.ActionEnum
import action.properties.StringProperty
import managers.Sandbox
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