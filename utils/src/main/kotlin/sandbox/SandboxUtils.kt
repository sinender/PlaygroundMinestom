package net.sinender.utils.sandbox

import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block

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