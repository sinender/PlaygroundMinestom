package net.sinender.app

import com.github.shynixn.mccoroutine.minestom.launch
import commands.HousingCommands
import managers.loadSandboxes
import managers.loadedSandboxes
import managers.startupDatabase
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerSkinInitEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import revxrsal.commands.minestom.MinestomLamp
import kotlin.math.min


fun main() {
    // Initialize the server
    val minecraftServer = MinecraftServer.init()
    MojangAuth.init()
    // Register Events (set spawn instance, teleport player at spawn)

    // Create the instance
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    //Essentially a void world
    instanceContainer.setBlock(0, 1, 0, Block.BARRIER)
    // Add an event callback to specify the spawning instance (and the spawn position)
    val globalEventHandler = MinecraftServer.getGlobalEventHandler()
    globalEventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event ->
        val player = event.player
        event.spawningInstance = instanceContainer
        player.respawnPoint = Pos(0.0, 3.0, 0.0)
    }

    globalEventHandler.addListener(PlayerMoveEvent::class.java) { event ->
        if (event.instance != instanceContainer) return@addListener
        event.isCancelled = true;
    }

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)

    val lamp = MinestomLamp.builder().build()
    lamp.register(HousingCommands())

    minecraftServer.launch {
        startupDatabase(
            "mongodb://localhost:27017",
            "test"
        )

        loadSandboxes()
    }
}