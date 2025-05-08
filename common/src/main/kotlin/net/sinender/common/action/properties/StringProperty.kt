package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.sinender.common.action.displayName
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.info
import net.sinender.utils.openChat
import java.lang.reflect.Field

class StringProperty : ActionProperty<String>(
    StringPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: InventoryPreClickEvent,
        sandbox: Sandbox,
        player: Player,
        menu: Menu,
    ) {
        player.openChat(value(obj, field) ?: "", field.displayName()) { message ->
            value(obj, field, message, player)
            menu.open(player)
        }
    }
}

@Target(AnnotationTarget.FIELD)
annotation class StringPropertyAnnotation(
    val material: String = "STRING",
)