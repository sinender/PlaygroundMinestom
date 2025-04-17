package feature.events

import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.trait.PlayerEvent

enum class EventType(
    val clazz: Class<out PlayerEvent>
) {
    BLOCK_BREAK(PlayerBlockBreakEvent::class.java),
}