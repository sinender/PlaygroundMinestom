package net.sinender.app

import com.github.shynixn.mccoroutine.minestom.launch
import commands.HousingCommands
import createLobbyInstance
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

    createLobbyInstance()

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)

    val lamp = MinestomLamp.builder()
        .suggestionProviders() {
            it.addProvider(String.javaClass) { context ->
                for (sandbox in loadedSandboxes.values) {
                    if (sandbox.sandboxUUID.startsWith(context.input().peekUnquotedString())) {
                        return@addProvider listOf(sandbox.sandboxUUID)
                    }
                }
                return@addProvider emptyList()
            }
        }
        .build()
    lamp.register(HousingCommands())

    minecraftServer.launch {
        startupDatabase(
            "mongodb://localhost:27017",
            "test"
        )

        loadSandboxes()
    }
}