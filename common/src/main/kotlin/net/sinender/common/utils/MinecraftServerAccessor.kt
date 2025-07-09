package net.sinender.common.utils

import com.github.shynixn.mccoroutine.minestom.addSuspendingListener
import kotlinx.coroutines.asCoroutineDispatcher
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.PlayerEvent

lateinit var minecraftServer: MinecraftServer
val minecraftDispatcher = MinecraftServer.getSchedulerManager().asCoroutineDispatcher()

fun initMinecraftServer(server: MinecraftServer) {
    minecraftServer = server
    // Additional initialization logic can be added here if needed
}

fun <E : Event> EventNode<E>.susListener(
    eventType: Class<out E>,
    listener: suspend (E) -> Unit
) {
    this.addSuspendingListener(minecraftServer, eventType) { listener(it) }
}