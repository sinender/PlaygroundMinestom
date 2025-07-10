package net.sinender.common.feature.variables

import com.ezylang.evalex.Expression
import net.sinender.common.feature.evalex.PGExpression
import net.sinender.common.postgres.Sandbox


fun Sandbox.getVar(key: String): Any? {
    return this.globalVariables.getOrElse(key) { 0 }
}

fun Sandbox.setVar(key: String, value: PGExpression) {
    this.globalVariables[key] = value.evaluate().value
}

fun Sandbox.listVars(): Set<String> {
    return this.globalVariables.keys.toSet()
}