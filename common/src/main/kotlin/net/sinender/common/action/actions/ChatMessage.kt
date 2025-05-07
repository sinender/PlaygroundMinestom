package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.StringPropertyAnnotation
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.colorize

data class ChatMessage(
    @DisplayName("Message") @Description("The message to send to the player.")
    @StringPropertyAnnotation
    val message: String = "<yellow>Hello, world!"
): Action(
    ActionEnum.CHAT_MESSAGE,
    "Display Chat Message",
    "Sends a chat message to the player.",
    Material.PAPER
) {
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        player?.sendMessage(colorize(message))
    }
}
