package net.sinender.common.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.sinender.common.feature.stats.getStat
import net.sinender.common.feature.stats.listStats
import net.sinender.common.managers.getSandbox
import net.sinender.utils.*
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class StatCommands: CommandInstance {
    @Command("viewstats <player>")
    fun viewPlayerStats(actor: MinestomCommandActor, player: String) {
        actor.requirePlayer()

        if (actor.asPlayer()!!.getSandbox() == null) {
            actor.err("You need to be in a sandbox instance!")
            return
        }

        for (instance in MinecraftServer.getInstanceManager().instances) {
            instance.players.filter { it.username == player }.forEach {
                actor.success("${it.username}'s Stats:")
                for (stat in it.listStats(actor.asPlayer()!!.getSandbox())!!) {
                    actor.sendRawMessage(Component.text("   $stat: ", successColor!!)
                            .append(Component.text(it.getStat(stat, actor.asPlayer()!!.getSandbox()) ?: "", NamedTextColor.WHITE)))
                }
            }
        }
    }

    @Command("viewglobalstats")
    fun viewGlobalStats(actor: MinestomCommandActor) {
        actor.requirePlayer()

        if (actor.asPlayer()!!.getSandbox() == null) {
            actor.err("You need to be in a sandbox instance!")
            return
        }

        actor.success("Global Stats:")
        for (stat in actor.asPlayer()!!.getSandbox()!!.listStats()) {
            actor.sendRawMessage(Component.text("   $stat: ", successColor!!)
                .append(Component.text(actor.asPlayer()!!.getSandbox()!!.getStat(stat) ?: "", NamedTextColor.WHITE)))
        }
    }
}