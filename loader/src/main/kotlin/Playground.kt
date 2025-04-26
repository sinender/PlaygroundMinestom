package net.sinender.app

import com.github.shynixn.mccoroutine.minestom.launch
import net.sinender.lobby.createLobbyInstance
import net.sinender.common.managers.loadSandboxes
import net.sinender.common.managers.startupDatabase
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.sinender.common.commands.CommandInstance
import org.reflections.Reflections
import revxrsal.commands.minestom.MinestomLamp


fun main() {
    // Initialize the server
    val minecraftServer = MinecraftServer.init()
    MojangAuth.init()

    createLobbyInstance()

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)

    val lamp = MinestomLamp.builder().build()

    val reflections = Reflections("net.sinender.common.commands")
    val commandClasses = reflections.getSubTypesOf(CommandInstance::class.java)
    for (commandClass in commandClasses) {
        try {
            val command = commandClass.getDeclaredConstructor().newInstance()
            lamp.register(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    minecraftServer.launch {
        startupDatabase(
            "mongodb://localhost:27017",
            "test"
        )

        loadSandboxes()
    }
}