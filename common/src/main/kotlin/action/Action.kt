package action

import managers.Sandbox
import menu.MenuItem
import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.bson.Document

open class Action(
    val id: String,
    val name: String,
    val description: String,
    val icon: Material
) {
    var comment = ""
    val properties = mutableListOf<ActionProperty>()

    open fun execute(player: Player, sandbox: Sandbox) {
        // Do nothing
    }

    fun createDisplayItem(): MenuItem {
        val builder = MenuItem(ItemStack.of(icon)) {
            // Will open the action editor
        }
        builder.name("<yellow>$name")
        builder.description(comment)
        builder.info("&eSettings", "")
        for (property in properties) {
            builder.info(property.displayName, property.value.toString())
        }
        if (properties.isNotEmpty()) {
            builder.action(ClickType.LEFT_CLICK, "click to edit")
        }
        builder.action(ClickType.RIGHT_CLICK, "click to remove")

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
        doc["id"] = id
        doc["comment"] = comment
        val propertiesDoc = Document()
        properties.forEach {
            propertiesDoc[it.id] = it.value
        }
        doc["properties"] = propertiesDoc
        return doc
    }

    fun fromDocument(doc: Document): Action {
        comment = doc.getString("comment")
        val propertiesDoc = doc.get("properties", Document::class.java)
        properties.forEach { prop ->
            prop.value = propertiesDoc[prop.id]
            if (prop.value == null) return@forEach
            this.javaClass.declaredFields.forEach { field ->
                if (field.name == prop.id) {
                    if (field.type.isEnum) {
                        if (prop.value is String) {
                            field.set(this, field.type.enumConstants.first { it.toString() == prop.value });
                        } else {
                            field.set(this, prop.value)
                        }
                        return@forEach
                    }
                    field.isAccessible = true
                    field.set(this, prop.value)
                }
            }
        }

        return this
    }

    fun comment(comment: String): Action {
        this.comment = comment
        return this
    }

    fun property(property: ActionProperty): Action {
        properties.add(property)
        return this
    }
}

data class ActionProperty(
    val id: String,
    var displayName: String = "",
    var type: PropertyType = PropertyType.STRING,
    var value: Any? = null
) {
    fun displayName(displayName: String): ActionProperty {
        this.displayName = displayName
        return this
    }

    fun type(type: PropertyType): ActionProperty {
        this.type = type
        return this
    }

    fun value(value: Any): ActionProperty {
        this.value = value
        return this
    }
}

enum class PropertyType {
    STRING, INT, DOUBLE, BOOLEAN
}
