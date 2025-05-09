package net.sinender.common.feature.variables

import net.sinender.common.postgres.Sandbox


fun Sandbox.getVar(key: String): String {
    return this.globalVars.getOrElse(key) { "0" }
}

fun Sandbox.setVar(key: String, value: String) {
    this.globalVars[key] = value
}

fun Sandbox.listVars(): Set<String> {
    return this.globalVars.keys.toSet()
}