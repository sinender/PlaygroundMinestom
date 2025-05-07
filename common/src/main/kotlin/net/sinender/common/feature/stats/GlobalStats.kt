package net.sinender.common.feature.stats

import net.sinender.common.postgres.Sandbox


fun Sandbox.getStat(key: String): String {
    return this.globalStats.getOrElse(key) { "0" }
}

fun Sandbox.setStat(key: String, value: String) {
    this.globalStats[key] = value
}

fun Sandbox.listStats(): Set<String> {
    return this.globalStats.keys.toSet()
}