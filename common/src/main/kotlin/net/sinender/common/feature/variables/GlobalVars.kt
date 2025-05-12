package net.sinender.common.feature.variables

import net.sinender.common.postgres.Sandbox


fun Sandbox.getVar(key: String): String {
    return this.globalVariables.getOrElse(key) { "0" }
}

fun Sandbox.setVar(key: String, value: String) {
    this.globalVariables[key] = value
}

fun Sandbox.listVars(): Set<String> {
    return this.globalVariables.keys.toSet()
}