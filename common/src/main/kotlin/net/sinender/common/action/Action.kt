package net.sinender.common.action

import net.minestom.server.entity.Entity
import net.sinender.common.managers.Sandbox
import net.sinender.common.menu.MenuItem
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.bson.Document

abstract class Action(
    val enum: ActionEnum,
    val name: String,
    val description: String,
    val icon: Material
) {
    var comment = ""
    val properties = mutableListOf<ActionProperty<*>>()

    // this would work, right?
    abstract fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?)

    fun createDisplayItem(): MenuItem {
        val builder = MenuItem(ItemStack.of(icon)) {
            // Will open the action editor
        }
        builder.name("<yellow>$name")
        builder.description(comment)
        if (properties.isNotEmpty()) {
            builder.info("<yellow>Settings", "")
            for (property in properties) {
                builder.info(property.displayName, property.value.toString())
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


    fun toDocument(): Document {
        val doc = Document()
        doc["id"] = enum.id
        doc["comment"] = comment
        val propertiesDoc = Document()
        properties.forEach {
            propertiesDoc[it.id] = it.toValue()
        }
        doc["properties"] = propertiesDoc
        return doc
    }

    fun fromDocument(doc: Document): Action {
        comment = doc.getString("comment")
        val propertiesDoc = doc.get("properties", Document::class.java)
        properties.forEach { prop ->
            prop.fromValue(propertiesDoc[prop.id])
            if (prop.value == null) return@forEach
        }

        return this
    }

    fun <V> getValue(key: String, clazz: Class<V>): V? {
        require(!clazz.isInstance(ActionProperty::class.java)) { "Cannot use an ActionProperty as a class type" }
        for (property in properties) {
            if (property.id.equals(key)) {
                return property.value as V?
            }
        }

        return null
    }


    fun <V : ActionProperty<*>?> getProperty(id: String?, clazz: Class<V>): V? {
        for (property in properties) {
            if (property.id.equals(id) && property.javaClass === clazz) {
                return property as V
            }
        }
        return null
    }

    fun comment(comment: String): Action {
        this.comment = comment
        return this
    }

    fun property(property: ActionProperty<*>): Action {
        properties.add(property)
        return this
    }

    open fun requiresEvent(): Boolean {
        return false
    }
}
