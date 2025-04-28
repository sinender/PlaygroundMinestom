package net.sinender.common.action

import net.sinender.common.action.actions.*
import org.bson.Document

enum class ActionEnum(
    val id: String,
    val clazz: Class<out Action>
) {
    CANCEL_EVENT("cancel_event", CancelEvent::class.java),
    CHAT_MESSAGE("chat_action", ChatMessage::class.java),
    ACTIONBAR("actionbar", ActionBar::class.java),
    POTION_EFFECT("potion_effect", PotionEffect::class.java),
    PLAYER_STAT("player_stat", PlayerStat::class.java)

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