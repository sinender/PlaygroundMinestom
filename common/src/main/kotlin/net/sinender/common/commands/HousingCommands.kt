package net.sinender.common.commands

import net.kyori.adventure.key.Key
import net.kyori.adventure.resource.ResourcePackInfo
import net.kyori.adventure.resource.ResourcePackRequest
import net.kyori.adventure.resource.ResourcePackStatus
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.*
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.Material
import net.minestom.server.timer.TaskSchedule
import net.sinender.common.feature.housingMenu.PlaygroundMenu
import net.sinender.common.managers.*
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem
import net.sinender.utils.setInstanceSafe
import net.sinender.utils.success
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor


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
        actor.asPlayer()!!.gameMode = GameMode.CREATIVE

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