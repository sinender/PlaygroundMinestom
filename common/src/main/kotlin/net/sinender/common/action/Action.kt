package net.sinender.common.action

import com.ezylang.evalex.data.EvaluationValue
import com.github.shynixn.mccoroutine.minestom.launch
import com.google.gson.JsonObject
import kotlinx.coroutines.invoke
import net.minestom.server.entity.Entity
import net.sinender.common.menu.MenuItem
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.postgres.Sandbox
import net.sinender.common.utils.minecraftDispatcher
import net.sinender.common.utils.minecraftServer
import java.lang.reflect.Field

abstract class Action(
    val enum: ActionEnum,
    @Transient val name: String,
    @Transient val description: String,
    @Transient val icon: Material
) {
    var comment = ""

    val properties: List<Pair<Field, ActionProperty<*>>>
        get() {
            val properties = mutableListOf<Pair<Field, ActionProperty<*>>>()
            for (field in this.javaClass.declaredFields) {
                for (property in ActionProperties.entries) {
                    if (field.isAnnotationPresent(property.annotation.java)) {
                        properties.add(Pair(field, property))
                    }
                }
            }
            return properties
        }

    fun List<Pair<Field, ActionProperty<*>>>.withValue(obj: Any?): Pair<Field, ActionProperty<*>> {
        return this.first {
            it.first.isAccessible = true
            val value = it.first.get(this@Action)
            value == obj
        }
    }

    open fun syncExecute(
        entity: Entity, player: Player?, sandbox: Sandbox, event: Event?,
        expression: (String) -> PGExpression = { PGExpression(it, entity, player, sandbox, event) }
    ): EvaluationValue? {
        minecraftServer.launch {
            execute(entity, player, sandbox, event, expression)
        }

        return null
    }

    abstract suspend fun execute(
        entity: Entity, player: Player?, sandbox: Sandbox, event: Event?, expression: (String) -> PGExpression = {
            PGExpression(it, entity, player, sandbox, event)
        }
    )

    fun createDisplayItem(): MenuItem {
        val builder = MenuItem(ItemStack.of(icon))
        builder.name("<yellow>$name")
        builder.description(comment)
        if (properties.isNotEmpty()) {
            builder.info("<yellow>Settings", "")

            for (field in this.javaClass.declaredFields) {
                val displayName = field.getAnnotation(DisplayName::class.java)
                if (displayName != null) {
                    builder.info(displayName.value, field.displayValue(this))
                }
            }

            builder.action(ClickType.LEFT_CLICK, "to edit")
        }
        builder.action(ClickType.RIGHT_CLICK, "to remove")

        return builder
    }

    fun createAddDisplayItem(): MenuItem {
        val builder = MenuItem(ItemStack.of(icon))
        builder.name("<yellow>$name")
        builder.description(description)
        builder.action(ClickType.LEFT_CLICK, "to add")

        return builder
    }

    fun comment(comment: String): Action {
        this.comment = comment
        return this
    }

    open fun requiresEvent(): Boolean {
        return false
    }
}
