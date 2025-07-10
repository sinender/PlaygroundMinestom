package net.sinender.common.action.properties

import com.ezylang.evalex.data.EvaluationValue
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionProperty
import net.sinender.common.action.displayName
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.menu.Menu
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.openChat
import java.lang.reflect.Field

class AnyProperty : ActionProperty<String>(
    AnyPropertyAnnotation::class
) {
    override fun runnable(
        field: Field,
        obj: Any,
        event: InventoryPreClickEvent,
        sandbox: Sandbox,
        player: Player,
        menu: Menu
    ) {
        player.openChat(value(obj, field) ?: "", field.displayName()) { message ->
            value(obj, field, message, player)
            menu.open(player)
        }
    }
}

fun Action.anyValue(expression: PGExpression): EvaluationValue {
    return expression.evaluate()
}

@Target(AnnotationTarget.FIELD)
annotation class AnyPropertyAnnotation(
    val material: String = "MAP",
)