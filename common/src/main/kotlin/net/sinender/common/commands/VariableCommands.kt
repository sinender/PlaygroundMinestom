package net.sinender.common.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.sinender.common.feature.variables.getVar
import net.sinender.common.feature.variables.listVars
import net.sinender.common.managers.getSandbox
import net.sinender.utils.*
import revxrsal.commands.annotation.Command
import revxrsal.commands.minestom.actor.MinestomCommandActor

class VariableCommands: CommandInstance {
    @Command("viewvars <player>")
    fun viewPlayerVars(actor: MinestomCommandActor, player: String) {
        actor.requirePlayer()

        if (actor.asPlayer()!!.getSandbox() == null) {
            actor.err("You need to be in a sandbox instance!")
            return
        }

        val instance = actor.asPlayer()!!.getSandbox()?.instance ?: return actor.err("You need to be in a sandbox instance!")

        instance.players.first { it.username.equals(player, true) }.apply {
            actor.success("${username}'s Variables:")
            for (variable in listVars()?: return) {
                actor.sendRawMessage(Component.text("   $variable: ", successColor!!)
                    .append(Component.text(getVar(variable, actor.asPlayer()!!.getSandbox()) ?: "", NamedTextColor.WHITE)))
            }
        }
    }

    @Command("viewglobalvariables")
    fun viewGlobalVars(actor: MinestomCommandActor) {
        actor.requirePlayer()

        if (actor.asPlayer()!!.getSandbox() == null) {
            actor.err("You need to be in a sandbox instance!")
            return
        }

        actor.success("Global Variables:")
        for (variable in actor.asPlayer()!!.getSandbox()!!.listVars()) {
            actor.sendRawMessage(Component.text("   $variable: ", successColor!!)
                .append(Component.text(actor.asPlayer()!!.getSandbox()!!.getVar(variable) ?: "", NamedTextColor.WHITE)))
        }
    }
}