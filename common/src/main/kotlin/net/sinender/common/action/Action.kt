package net.sinender.common.action

import com.google.gson.JsonObject
import net.minestom.server.entity.Entity
import net.sinender.common.menu.MenuItem
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.sinender.common.postgres.Sandbox
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
            for (property in ActionProperties.entries) {
                for (field in this.javaClass.declaredFields) {
                    if (field.isAnnotationPresent(property.annotation.java)) {
                        properties.add(Pair(field, property))
                    }
                }
            }
            return properties
        }

    // this would work, right?
    abstract fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?)

    fun createDisplayItem(): MenuItem {
        val builder = MenuItem(ItemStack.of(icon)) {
            // Will open the action editor
        }
        builder.name("<yellow>$name")
        builder.description(comment)
        if (this.javaClass.declaredFields.isNotEmpty()) {
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
        val builder = MenuItem(ItemStack.of(icon)) {
            // Will open the action editor
        }
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
