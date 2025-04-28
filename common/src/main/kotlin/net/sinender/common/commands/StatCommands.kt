package net.sinender.common.commands

import net.minestom.server.MinecraftServer
import net.sinender.common.feature.stats.getStat
import net.sinender.common.feature.stats.listStats
import net.sinender.common.managers.getSandbox
import net.sinender.utils.err
import net.sinender.utils.success
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
            for (instancePlayer in instance.players) {
                if (instancePlayer.username == player) {
                    for (stat in instancePlayer.listStats(actor.asPlayer()!!.getSandbox())!!) {
                        actor.success("$stat: " + instancePlayer.getStat(stat, actor.asPlayer()!!.getSandbox()))
                    }
                }
            }
        }
    }
}