package net.sinender.common.commands

import net.sinender.common.feature.housingMenu.PlaygroundMenu
import net.sinender.common.managers.*
import net.hollowcube.polar.PolarLoader
import net.hollowcube.polar.PolarWriter
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.*
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.event.trait.PlayerEvent
import net.sinender.common.feature.events.EventType
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.feature.npc.npcs.PlayerNpc
import net.sinender.utils.setInstanceSafe
import net.sinender.utils.success
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor
import java.time.Duration
import java.time.temporal.ChronoUnit


class HousingCommands : CommandInstance {
    @Command("playground create")
    suspend fun create(actor: MinestomCommandActor) {
        var sandbox = createSandbox(actor.asPlayer()?: return actor.error("You must be a player to use this command."))
        if (sandbox.instance == null) return actor.error("An error occurred while creating your sandbox instance.")

        actor.asPlayer()!!.setInstance(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE

        sandbox.startEventListener()

        actor.success("You have been teleported to your sandbox instance!")
    }

    @Command("playground goto <id>")
    suspend fun goto(actor: MinestomCommandActor, id: String) {
        actor.requirePlayer()

        val sandbox = loadedSandboxes[id] ?: return actor.error("That sandbox does not exist.")
        if (sandbox.instance == null) sandbox.loadInstance()
        if (sandbox.instance == null) return actor.error("An error occurred while teleporting to your sandbox.")
        actor.asPlayer()!!.setInstanceSafe(sandbox.instance!!, Pos(0.0, 61.0, 0.0))
        actor.asPlayer()!!.gameMode = GameMode.SURVIVAL

        sandbox.save()
        sandbox.startEventListener()
        sandbox.spawnNPCs()

        actor.success("You have been teleported to sandbox instance <dark_gray>(ID: ${id})</dark_gray>!")
    }

    @Command("playground menu")
    fun menu(actor: MinestomCommandActor) {
        actor.requirePlayer()
        loadedSandboxes.filter { it.value.instance == actor.asPlayer()!!.instance }.forEach {
            actor.success("Sandbox ID: ${it.key}")
            PlaygroundMenu().open(actor.asPlayer()!!)
        }
    }

    @Command("playground test")
    fun test(actor: MinestomCommandActor) {
        actor.requirePlayer()
        loadedSandboxes.filter { it.value.instance == actor.asPlayer()!!.instance }.forEach { (_, sandbox) ->
            var npc = PlayerNpc(
                NpcEntity.getCurrentID(sandbox),
                actor.asPlayer()!!.position
            )

            npc.spawn(sandbox)
            sandbox.npcs.add(npc)
        }
    }

    @Command("playground save")
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