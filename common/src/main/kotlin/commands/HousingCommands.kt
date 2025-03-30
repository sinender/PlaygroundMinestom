import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.sinender.utils.success
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class HousingCommands {
    @Command("housing create")
    fun housing(actor: MinestomCommandActor) {
        actor.requirePlayer()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()

        instanceContainer.setChunkSupplier(::LightingChunk)

        createTemplatePlatform(instanceContainer)

        actor.asPlayer()!!.setInstance(instanceContainer, Pos(0.0, 61.0, 0.0))

        actor.asPlayer()!!.gameMode = GameMode.CREATIVE

        actor.success("You have been teleported to your housing instance!")
    }

    private fun createTemplatePlatform(instanceContainer: InstanceContainer) {
        val platformSize = 15
        val startX = -platformSize / 2
        val startZ = -platformSize / 2

        for (x in startX..-startX) {
            for (z in startZ..-startZ) {

                instanceContainer.setBlock(x, 59, z, Block.STONE)
                val grassType = if (Math.random() > 0.25) Block.GRASS_BLOCK else Block.COARSE_DIRT
                instanceContainer.setBlock(x, 60, z, grassType)
                if (Math.random() < 0.2) instanceContainer.setBlock(x, 61, z, Block.SHORT_GRASS)
            }
        }
    }
}