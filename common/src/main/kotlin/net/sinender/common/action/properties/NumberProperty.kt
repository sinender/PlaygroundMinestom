package net.sinender.common.action.properties

import net.minestom.server.entity.Entity
import net.sinender.common.action.ActionProperty
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.sinender.common.action.Action
import net.sinender.common.action.displayName
import net.sinender.common.action.getFieldFromAnnotation
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.*
import java.lang.reflect.Field

class NumberProperty : ActionProperty<String>(
    NumberPropertyAnnotation::class
) {
    override fun runnable(field: Field, obj: Any, event: InventoryPreClickEvent, sandbox: Sandbox, player: Player, menu: Menu) {
        player.openChat(value(obj, field).toString(), field.displayName()) { message ->
            value(obj, field, message, player)
            menu.open(player)
        }
    }
}

fun Action.doubleValue(expression: PGExpression): Double {
    val numProp = this.properties.withValue(expression.expressionString).first.getAnnotation(NumberPropertyAnnotation::class.java)
    val value = expression.evaluate().numberValue.toDouble()

    if (value < numProp.min || value > numProp.max) {
        throw IllegalArgumentException("Value $value is out of bounds [${numProp.min}, ${numProp.max}]")
    }

    return value
}

@Target(AnnotationTarget.FIELD)
annotation class NumberPropertyAnnotation(
    val material: String = "BOOK",
    val min: Double = Double.MIN_VALUE,
    val max: Double = Double.MAX_VALUE,
)