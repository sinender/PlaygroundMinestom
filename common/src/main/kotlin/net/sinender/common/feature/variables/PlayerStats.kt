package net.sinender.common.feature.variables

import net.minestom.server.entity.Player
import net.sinender.common.managers.getSandbox
import net.sinender.common.postgres.Sandbox

fun Player.getVar(key: String, sandbox: Sandbox? = this.getSandbox()): String? {
    if (sandbox == null) return null
    val playerVars = sandbox.playerVars.getOrPut(this.uuid.toString()) { mutableMapOf() }
    return playerVars.getOrElse(key) { "0" }
}

fun Player.setVar(key: String, value: String, sandbox: Sandbox? = this.getSandbox()) {
    if (sandbox == null) return
    val playerVars = sandbox.playerVars.getOrPut(this.uuid.toString()) { mutableMapOf() }
    playerVars[key] = value
}

fun Player.listVars(sandbox: Sandbox? = this.getSandbox()): Set<String>? {
    if (sandbox == null) return null
    val playerVars = sandbox.playerVars.getOrPut(this.uuid.toString()) { mutableMapOf() }
    return playerVars.keys.toSet()
}