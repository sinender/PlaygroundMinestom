import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.event.player.PlayerCommandEvent
import net.sinender.utils.send

data class TempStorageForListener(
    var listener: EventListener<PlayerChatEvent>?,
    var listener2: EventListener<PlayerCommandEvent>?
) {
    fun remove() {
        MinecraftServer.getGlobalEventHandler().removeListener(listener ?: return)
        MinecraftServer.getGlobalEventHandler().removeListener(listener2 ?: return)
    }
}

fun Player.openChat(
    previous: String,
    action: (String) -> Unit,
) {
    this.closeInventory()

    var clazz = TempStorageForListener(null, null)

    this.sendMessage(
        MiniMessage.miniMessage().deserialize(
            " <click:suggest_command:'${previous}'><hover:show_text:'<aqua>Click to paste previous value'><aqua>[PREVIOUS]</aqua></hover></click>" +
                    " <click:run_command:'/cancelinput'><hover:show_text:'<red>Click to cancel'><red>[CANCEL]</red></hover></click>"
        )
    )
    var listener = EventListener.of(PlayerChatEvent::class.java) { event ->
        if (event.player != this) return@of
        event.isCancelled = true
        action(event.rawMessage)
        clazz.remove()
    }
    var listener2 = EventListener.of(PlayerCommandEvent::class.java) { event ->
        if (event.player != this) return@of
        event.isCancelled = true
        clazz.remove()
        this.send(
            "<red>Input cancelled"
        )
        action(previous)
    }

    clazz.listener = listener
    clazz.listener2 = listener2

    MinecraftServer.getGlobalEventHandler().addListener(listener)
    MinecraftServer.getGlobalEventHandler().addListener(listener2)
}