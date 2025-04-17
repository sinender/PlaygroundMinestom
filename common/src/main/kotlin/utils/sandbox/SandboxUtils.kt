package utils.sandbox

import com.mongodb.client.model.Filters.eq
import managers.Sandbox
import managers.database
import net.hollowcube.polar.AnvilPolar.*
import net.hollowcube.polar.PolarLoader
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import java.nio.file.Path

fun createTemplatePlatform(instanceContainer: InstanceContainer) {
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

suspend fun Sandbox.save() {
    val collection = database?.getCollection<Sandbox>("sandboxes")
    collection?.replaceOne(eq("sandboxUUID", this.sandboxUUID), this)
}

fun Sandbox.loadInstance(chunkLoader: IChunkLoader) {
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.chunkLoader = chunkLoader
    instanceContainer.setChunkSupplier(::LightingChunk)

    this.instance = instanceContainer
}