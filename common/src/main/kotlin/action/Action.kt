package action

import managers.Sandbox
import net.minestom.server.entity.Player
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

    fun fromDocument(doc: Document) {
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
                    field.set(this, prop.value)
                }
            }
        }
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
