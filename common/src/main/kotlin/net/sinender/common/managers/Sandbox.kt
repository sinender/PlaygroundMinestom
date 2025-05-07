package net.sinender.common.managers

import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import net.hollowcube.polar.AnvilPolar
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarReader
import net.hollowcube.polar.PolarWriter
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.sinender.common.postgres.Sandbox
import net.sinender.common.postgres.sandboxInstanceService
import net.sinender.common.postgres.sandboxService
import net.sinender.common.sandbox.createTemplatePlatform
import java.nio.file.Path
import java.util.*

/**
 * Map of currently loaded sandboxes by sandbox UUID
 */
val loadedSandboxes = mutableMapOf<String, Sandbox>()

/**
 * Creates a new sandbox with the specified owner
 */
suspend fun createSandbox(player: Player): Sandbox {
    var sandbox = sandboxService.createSandbox(
        player.username + "'s Sandbox",
        UUID.randomUUID().toString(),
        player.uuid.toString(),
    )

    loadedSandboxes[sandbox.sandboxUUID] = sandbox

    sandbox.instance = createInstance(PolarLoader(AnvilPolar.anvilToPolar(Path.of("template_sandbox_world"))))
    sandbox.instance?.let { instance ->
        createTemplatePlatform(instance)
        instance.saveChunksToStorage()
    }

    sandboxInstanceService.createSandbox(
        sandbox.sandboxUUID,
        PolarWriter.write((sandbox.instance!!.chunkLoader as PolarLoader).world())
    )

    return sandbox
}

/**
 * Loads the instance data from database
 */
suspend fun Sandbox.loadInstance() {
    val sbInstance = sandboxInstanceService.getSandboxByUUID(this.sandboxUUID) ?: return
    this.instance = createInstance(PolarLoader(PolarReader.read(sbInstance.instanceArray)))
}

fun createInstance(chunkLoader: IChunkLoader): InstanceContainer {
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.chunkLoader = chunkLoader
    instanceContainer.setChunkSupplier(::LightingChunk)

    return instanceContainer
}

/**
 * Saves the instance data to database
 */
suspend fun Sandbox.saveInstance() {
    instance?.saveChunksToStorage() ?: return // Ensure instance is not null before saving
    val chunkLoader = instance?.chunkLoader ?: return // Ensure chunk loader is not null before saving
    val instanceArray = PolarWriter.write((chunkLoader as PolarLoader).world())

    sandboxInstanceService.updateSandbox(sandboxUUID, instanceArray)
}

suspend fun Sandbox.save() {
    saveInstance()

    sandboxService.updateSandbox(
        this.sandboxUUID,
        this
    )
}

suspend fun loadSandboxes() {
    val sandboxes = sandboxService.getAllSandboxes()
    for (sandbox in sandboxes) {
        loadedSandboxes[sandbox.sandboxUUID] = sandbox
        sandbox.loadInstance()
    }
}

fun Player.getSandbox(): Sandbox? {
    return loadedSandboxes.values.firstOrNull { it.instance == this.instance }
}