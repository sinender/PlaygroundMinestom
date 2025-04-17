package action

import action.actions.ChatMessage
import org.bson.Document

enum class ActionEnum(
    val id: String,
    val clazz: Class<out Action>
) {
    CHAT_MESSAGE("chat_action", ChatMessage::class.java)

    ;

    companion object {
        fun fromId(id: String): ActionEnum? {
            return entries.firstOrNull { it.id == id }
        }
    }

    fun fromDocument(doc: Document): Action {
        return clazz.getDeclaredConstructor().newInstance().fromDocument(doc)
    }
}