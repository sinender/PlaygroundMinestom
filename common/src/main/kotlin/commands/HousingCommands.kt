package commands

import com.mongodb.client.model.Filters.eq
import feature.housingMenu.HousingMenuMain
import kotlinx.coroutines.flow.firstOrNull
import managers.Sandbox
import managers.SandboxInstance
import managers.createSandbox
import managers.database
import net.hollowcube.polar.AnvilPolar
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarReader
import net.hollowcube.polar.PolarWriter
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.sinender.utils.success
import org.bson.types.ObjectId
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class HousingCommands {
    @Command("housing create")
    suspend fun housing(actor: MinestomCommandActor) {
        actor.requirePlayer()

        var sandbox = createSandbox(actor.asPlayer()?.uuid.toString())

        database?.getCollection<Sandbox>("sandboxes")?.insertOne(sandbox)
        if (sandbox.instance == null) return actor.error(
            "An error occurred while creating your sandbox."
        )
        database?.getCollection<SandboxInstance>("sandboxInstances")?.insertOne(
            SandboxInstance(
                sandbox.id,
                sandbox.sandboxUUID,
                PolarWriter.write((sandbox.instance!!.chunkLoader as PolarLoader).world())
            )
        )

        actor.asPlayer()!!.setInstance(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE


        actor.success("You have been teleported to your sandbox instance!")
    }

    @Command("housing goto <id>")
    suspend fun goto(actor: MinestomCommandActor, id: String) {
        actor.requirePlayer()

        val sandbox = database?.getCollection<Sandbox>("sandboxes")?.find(eq("sandboxUUID", id))?.firstOrNull()
            ?: return actor.error("An error occurred whilst loading your sandbox.")
        sandbox.loadInstance()
        if (sandbox.instance == null) return actor.error("An error occurred while teleporting to your sandbox.")
        actor.asPlayer()!!.setInstance(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE

        actor.success("You have been teleported to sandbox instance <dark_gray>(ID: ${id})</dark_gray>!")
    }

    @Command("housing test")
    fun test(actor: MinestomCommandActor) {
        actor.requirePlayer()
        HousingMenuMain().open(actor.asPlayer()!!)
    }


}