package commands

import action.actions.ChatMessage
import com.mongodb.client.model.Filters.eq
import feature.housingMenu.PlaygroundMenu
import kotlinx.coroutines.flow.firstOrNull
import managers.Sandbox
import managers.SandboxInstance
import managers.createSandbox
import managers.database
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarWriter
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.sinender.utils.success
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

        val collection = database?.getCollection<Sandbox>("sandboxes");
        val sandbox = collection?.find(eq("sandboxUUID", id))?.firstOrNull()
            ?: return actor.error("An error occurred whilst loading your sandbox.")
        sandbox.loadInstance()
        if (sandbox.instance == null) return actor.error("An error occurred while teleporting to your sandbox.")
        actor.asPlayer()!!.setInstance(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE

        if (sandbox.actions == null) sandbox.actions = mutableListOf()
        sandbox.actions?.add(ChatMessage("<yellow>Hello World!"))

        collection.replaceOne(eq("sandboxUUID", id), sandbox)

        actor.success("You have been teleported to sandbox instance <dark_gray>(ID: ${id})</dark_gray>!")
    }

    @Command("housing test")
    fun test(actor: MinestomCommandActor) {
        actor.requirePlayer()
        PlaygroundMenu().open(actor.asPlayer()!!)
    }


}