package net.sinender.common.commands

import net.sinender.common.feature.events.EventType
import net.sinender.common.feature.housingMenu.PlaygroundMenu
import net.sinender.common.managers.*
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarWriter
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.PlayerEvent
import net.sinender.common.feature.stats.getStat
import net.sinender.common.feature.stats.setStat
import net.sinender.utils.setInstanceSafe
import net.sinender.utils.success
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor
import java.util.*


class HousingCommands: CommandInstance {
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

        val sandbox = loadedSandboxes[id] ?: return actor.error("That sandbox does not exist.")
        sandbox.loadInstance()
        if (sandbox.instance == null) return actor.error("An error occurred while teleporting to your sandbox.")
        actor.asPlayer()!!.setInstanceSafe(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE
        sandbox.save()

        // Sandbox Event Listener
        var handler = MinecraftServer.getGlobalEventHandler();
        val node: EventNode<PlayerEvent> = EventNode.value(sandbox.sandboxUUID, EventFilter.PLAYER) {
            it.instance == sandbox.instance
        }
        handler.addChild(node)
        for (event in EventType.entries) {
            node.addListener(event.clazz) { e: Event ->
                sandbox.eventsList?.get(event.name)?.forEach { action ->
                    if (action.requiresEvent()) {
                        action.execute(e, actor.asPlayer()!!, sandbox)
                    } else {
                        action.execute(actor.asPlayer()!!, sandbox)
                    }
                }
            }
        }


        actor.success("You have been teleported to sandbox instance <dark_gray>(ID: ${id})</dark_gray>!")
    }

    @Command("housing test")
    fun test(actor: MinestomCommandActor) {
        actor.requirePlayer()
        loadedSandboxes.filter { it.value.instance == actor.asPlayer()!!.instance }.forEach {
            actor.success("Sandbox ID: ${it.key}")
//            PlaygroundMenu().open(actor.asPlayer()!!)

            actor.asPlayer()!!.setStat("another/test", "Hello, world!")
            actor.success(actor.asPlayer()!!.getStat("another/test") ?: "not in sandbox")
        }
    }

    @Command("housing save")
    suspend fun save(actor: MinestomCommandActor) {
        actor.requirePlayer()
        val sandbox = loadedSandboxes.filter { it.value.instance == actor.asPlayer()!!.instance }
        if (sandbox.isEmpty()) return actor.error("You are not in a sandbox instance.")
        sandbox.forEach {
            it.value.save();
            actor.success("Sandbox instance <dark_gray>(ID: ${it.key})</dark_gray> has been saved!")
        }
    }


}