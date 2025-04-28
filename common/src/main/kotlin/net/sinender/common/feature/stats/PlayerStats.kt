package net.sinender.common.feature.stats

import net.minestom.server.entity.Player
import net.sinender.common.managers.Sandbox
import net.sinender.common.managers.getSandbox

fun Player.getStat(key: String, sandbox: Sandbox? = this.getSandbox()): String? {
    if (sandbox == null) return null
    val playerStats = sandbox.playerStats!!.getOrPut(this.uuid.toString()) { mutableMapOf() }
    return playerStats.getOrElse(key) { "0" }
}

fun Player.setStat(key: String, value: String, sandbox: Sandbox? = this.getSandbox()) {
    if (sandbox == null) return
    val playerStats = sandbox.playerStats!!.getOrPut(this.uuid.toString()) { mutableMapOf() }
    playerStats[key] = value
}
