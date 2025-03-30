package managers

import action.Action
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.first
import net.hollowcube.polar.AnvilPolar
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarReader
import net.minestom.server.MinecraftServer
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.sinender.utils.sandbox.createTemplatePlatform
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.types.ObjectId
import java.nio.file.Path
import java.util.*

fun createSandbox(ownerUUID: String): Sandbox {
    val sandbox = Sandbox(ObjectId(), UUID.randomUUID().toString(), ownerUUID, mutableListOf())
    val instanceManager = MinecraftServer.getInstanceManager()
    val instanceContainer = instanceManager.createInstanceContainer()

    instanceContainer.chunkLoader = PolarLoader(AnvilPolar.anvilToPolar(Path.of("template_sandbox_world")))
    instanceContainer.setChunkSupplier(::LightingChunk)
    createTemplatePlatform(instanceContainer)
    instanceContainer.saveChunksToStorage()

    sandbox.instance = instanceContainer
    return sandbox
}

data class Sandbox(
    @BsonId
    val id: ObjectId,
    val sandboxUUID: String,
    val ownerUUID: String,
    var actions: MutableList<Document>? = mutableListOf(),
) {
    var instance: InstanceContainer? = null
    suspend fun loadInstance() {
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val collection = database?.getCollection<SandboxInstance>("sandboxInstances")

        var sbInstance = collection?.find<SandboxInstance>(eq("sandboxUUID", sandboxUUID))?.first()
        if (sbInstance != null) {
            instanceContainer.chunkLoader = PolarLoader(PolarReader.read(sbInstance.instanceArray))
            instanceContainer.setChunkSupplier(::LightingChunk)
            instance = instanceContainer
        }
    }
}

//aka the world
data class SandboxInstance(
    @BsonId
    val sandboxId: ObjectId,
    val sandboxUUID: String,
    val instanceArray: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SandboxInstance

        if (sandboxId != other.sandboxId) return false
        if (!instanceArray.contentEquals(other.instanceArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sandboxId.hashCode()
        result = 31 * result + instanceArray.contentHashCode()
        return result
    }
}
