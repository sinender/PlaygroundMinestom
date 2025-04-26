import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block

fun createLobbyInstance() {
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
}