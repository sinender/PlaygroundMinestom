package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.sinender.utils.isInteger
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.sinender.common.action.displayName
import net.sinender.common.action.getFieldFromAnnotation
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.err
import net.sinender.utils.info
import net.sinender.utils.openChat
import java.lang.reflect.Field

class IntegerProperty : ActionProperty<Int>(
    IntegerPropertyAnnotation::class
) {
    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        player.info("<green>Please type the value you want to set for ${field.displayName()}:")
        player.openChat(value(obj, field).toString()) { message ->
            if (!message.isInteger()) {
                player.err("The value provided is not a number.")
                return@openChat
            }

            val valueTemp = message.toInt()
            val min = field.getFieldFromAnnotation<Int>("min") ?: Int.MIN_VALUE
            val max = field.getFieldFromAnnotation<Int>("max") ?: Int.MAX_VALUE
            if (valueTemp < min || valueTemp > max) {
                player.err("The value must be between $min and $max")
                return@openChat
            }

            value(obj, field, valueTemp, player)
            menu.open(player)
        }
    }
}

@Target(AnnotationTarget.FIELD)
annotation class IntegerPropertyAnnotation(
    val material: String = "BOOK",
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
)