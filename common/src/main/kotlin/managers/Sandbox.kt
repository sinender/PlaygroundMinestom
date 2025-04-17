package managers

import action.Action
import action.ActionEnum
import com.mongodb.client.model.Filters.eq
import feature.events.EventType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import net.hollowcube.polar.AnvilPolar
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarReader
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import utils.sandbox.createTemplatePlatform
import utils.sandbox.loadInstance
import java.nio.file.Path
import java.util.*

/**
 * Map of currently loaded sandboxes by sandbox UUID
 */
val loadedSandboxes = mutableMapOf<String, Sandbox>()

/**
 * Creates a new sandbox with the specified owner
 */
fun createSandbox(ownerUUID: String): Sandbox {
    val sandbox = Sandbox(
        id = ObjectId(),
        sandboxUUID = UUID.randomUUID().toString(),
        ownerUUID = ownerUUID
    )

    sandbox.loadInstance(PolarLoader(AnvilPolar.anvilToPolar(Path.of("template_sandbox_world"))))
    sandbox.instance?.let { instance ->
        createTemplatePlatform(instance)
        instance.saveChunksToStorage()
    }

    return sandbox
}

/**
 * Loads all sandboxes from database into memory
 */
suspend fun loadSandboxes() {
    database?.getCollection<Sandbox>("sandboxes")?.find()?.toList()?.forEach { sandbox ->
        loadedSandboxes[sandbox.sandboxUUID] = sandbox
    }
}

/**
 * Represents a sandbox environment for a player
 */
data class Sandbox(
    @BsonId
    val id: ObjectId,
    val sandboxUUID: String,
    val ownerUUID: String,
    var events: MutableMap<String, MutableList<Document>>? = mutableMapOf(),
) {
    var instance: InstanceContainer? = null

    val eventsList: MutableMap<String, MutableList<Action>>?
        get() = events?.mapValues { (_, documents) ->
            documents.mapNotNull { doc ->
                ActionEnum.fromId(doc.getString("id"))?.fromDocument(doc)
            }.toMutableList()
        }?.toMutableMap()

    /**
     * Loads the instance data from database
     */
    suspend fun loadInstance() {
        val collection = database?.getCollection<SandboxInstance>("sandboxInstances") ?: return
        collection.find<SandboxInstance>(eq("sandboxUUID", sandboxUUID)).first().let { sbInstance ->
            this.loadInstance(PolarLoader(PolarReader.read(sbInstance.instanceArray)))
        }
    }
}

/**
 * Represents the saved state of a sandbox instance (world)
 */
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

fun Player.getSandbox(): Sandbox? {
    return loadedSandboxes.values.firstOrNull { it.instance == this.instance }
}