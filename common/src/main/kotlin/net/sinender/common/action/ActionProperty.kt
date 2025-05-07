package net.sinender.common.action

import net.sinender.common.feature.actionMenu.ActionEditMenu
import net.minestom.server.entity.Player
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.menu.Menu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.menuItem
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.success
import java.lang.reflect.Field
import kotlin.reflect.KClass

abstract class ActionProperty<V>(
    val annotation: KClass<out Annotation>
) {
    open fun displayValue(value: V?): String? {
        if (value == null) {
            return "&cNone"
        }
        return value.toString()
    }

    abstract fun runnable(
        field: Field,
        obj: Any,
        event: InventoryPreClickEvent,
        sandbox: Sandbox,
        player: Player,
        menu: Menu
    )

    open fun getDisplayItem(obj: Any, field: Field): MenuItem {
        var material = Material.BARRIER
        val materialString = field.getFieldFromAnnotation<String>("material")
        if (materialString != null) {
            material = Material.fromKey(materialString.lowercase())
        }

        val item = menuItem(material)
            .name("<yellow>${field.displayName()}")
            .description(field.description())
            .info("<yellow>Settings", "")
            .info(null, field.displayValue(obj))
            .action(ClickType.LEFT_CLICK, "to edit")

        return item
    }

    fun value(obj: Any, field: Field, value: V) {
        field.isAccessible = true
        field.set(obj, value)
    }

    fun value(obj: Any, field: Field, value: V, player: Player) {
        field.isAccessible = true
        field.set(obj, value)
        player.success("${field.displayName()} set to $value")
    }

    fun value(obj: Any, field: Field): V? {
        field.isAccessible = true
        return field.get(obj) as V?
    }

    fun displayValue(obj: Any, field: Field): String? {
        return this.displayValue(value(obj, field))
    }
}

fun <V> Field.getFieldFromAnnotation(name: String): V? {
    for (property in ActionProperties.entries) {
        if (this.isAnnotationPresent(property.annotation.java)) {
            val field = this.getAnnotation(property.annotation.java)
            if (field != null) {
                val value = field.javaClass.getDeclaredMethod(name).invoke(field)
                return value as V?
            }
        }
    }
    return null
}

fun Field.displayValue(obj: Any): String {
    for (property in ActionProperties.entries) {
        if (this.isAnnotationPresent(property.annotation.java)) {
            return property.displayValue(obj, this) ?: "<red>Not Set"
        }
    }
    this.isAccessible = true
    return this.get(this)?.toString() ?: "<red>Not Set"
}

fun Field.displayName(): String {
    val displayName = this.getAnnotation(DisplayName::class.java)
    if (displayName != null) {
        return displayName.value
    }
    return this.name
}

fun Field.description(): String {
    val description = this.getAnnotation(Description::class.java)
    if (description != null) {
        return description.value
    }
    return ""
}

@Target(AnnotationTarget.FIELD)
annotation class DisplayName(val value: String)

@Target(AnnotationTarget.FIELD)
annotation class Description(val value: String)