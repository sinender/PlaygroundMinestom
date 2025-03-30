package action.actions

import action.Action
import action.ActionProperty
import action.PropertyType
import managers.Sandbox
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.sinender.utils.colorize

data class ChatMessage(
    var message: String = "<yellow>Hello World!"
): Action(
    "chat_action",
    "Display Chat Message",
    "Sends a chat message to the player.",
    Material.PAPER
) {
    init {
        property(
            ActionProperty("message")
                .displayName("Message")
                .type(PropertyType.STRING)
                .value(message)
        )
    }

    override fun execute(player: Player, sandbox: Sandbox) {
        player.sendMessage(colorize(message))
    }
}
