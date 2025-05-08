package net.sinender.common.action.actions

import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.StringPropertyAnnotation
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.colorize

data class ActionBar(
    @DisplayName("Message") @Description("The message to show to the player.")
    @StringPropertyAnnotation
    var message: String = "<yellow>Hello, world!"
): Action(
    ActionEnum.ACTIONBAR,
    "Display Action Bar",
    "Sends a message through the action bar to the player.",
    Material.NAME_TAG,
) {
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        player?.sendActionBar(colorize(message))
    }
}