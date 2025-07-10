package net.sinender.common.action.properties

import net.sinender.common.action.ActionProperty
import net.sinender.utils.isInteger
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.sinender.common.action.displayName
import net.sinender.common.action.getFieldFromAnnotation
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.err
import net.sinender.utils.openChat
import java.lang.reflect.Field

class DoubleProperty : ActionProperty<Double>(
    DoublePropertyAnnotation::class
) {
    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        player.openChat(value(obj, field).toString(), field.displayName()) { message ->
            if (!message.isInteger()) {
                player.err("The value provided is not a number.")
                return@openChat
            }

            val min = field.getFieldFromAnnotation<Double>("min") ?: Double.MIN_VALUE
            val max = field.getFieldFromAnnotation<Double>("max") ?: Double.MAX_VALUE
            val valueTemp = message.toDoubleOrNull()
            if (valueTemp != null && (valueTemp < min || valueTemp > max)) {
                player.err("The value must be between $min and $max")
                return@openChat
            }

            value(obj, field, valueTemp!!, player)
            menu.open(player)
        }
    }
}

@Target(AnnotationTarget.FIELD)
annotation class DoublePropertyAnnotation(
    val material: String = "BOOK",
    val min: Double = Double.MIN_VALUE,
    val max: Double = Double.MAX_VALUE,
)