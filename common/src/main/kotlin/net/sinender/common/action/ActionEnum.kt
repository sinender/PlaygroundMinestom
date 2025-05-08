package net.sinender.common.action

import net.sinender.common.action.actions.*

enum class ActionEnum(
    val id: String,
    val clazz: Class<out Action>
) {
    CANCEL_EVENT("cancel_event", CancelEvent::class.java),
    CHAT_MESSAGE("chat_action", ChatMessage::class.java),
    ACTIONBAR("actionbar", ActionBar::class.java),
    POTION_EFFECT("potion_effect", PotionEffect::class.java),
    DAMAGE("damage", DamageAction::class.java),
    CHANGE_VELOCITY("change_velocity", ChangeVelocity::class.java),
    WAIT("wait", WaitAction::class.java),
    PLAYER_VAR("player_var", PlayerVar::class.java),
    GLOBAL_VAR("global_var", GlobalVar::class.java),
    ;

    companion object {
        fun fromId(id: String): ActionEnum? {
            return entries.firstOrNull { it.id == id }
        }
    }
}