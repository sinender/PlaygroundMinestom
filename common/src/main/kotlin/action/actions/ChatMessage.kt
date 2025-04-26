package action.actions

import action.Action
import action.ActionEnum
import action.properties.StringProperty
import managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.sinender.utils.colorize

class ChatMessage: Action(
    ActionEnum.CHAT_MESSAGE,
    "Display Chat Message",
    "Sends a chat message to the player.",
    Material.PAPER
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
        player.sendMessage(colorize(getValue("message", String::class.java)?:""))
    }
}
