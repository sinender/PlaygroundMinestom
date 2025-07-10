package net.sinender.common.feature.evalex

import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.FunctionDictionaryIfc
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.parser.Token
import net.sinender.common.feature.variables.getVar

object VariableFunctions {
    fun apply(functionDictionary: FunctionDictionaryIfc) {
        functionDictionary.addFunction("var.player", VarPlayer())
        functionDictionary.addFunction("var.global", VarGlobal())
    }

    @FunctionParameter(name = "key")
    class VarPlayer : AbstractFunction() {
        override fun evaluate(
            expression: Expression?,
            functionToken: Token?,
            vararg parameterValues: EvaluationValue?
        ): EvaluationValue {
            if (expression is PGExpression) {
                val key = parameterValues[0]?.stringValue ?: return EvaluationValue.NULL_VALUE
                val player = expression.player ?: return EvaluationValue.NULL_VALUE
                val sandbox = expression.sandbox

                // Retrieve the variable from the player's sandbox
                val value = player.getVar(key, sandbox)
                return EvaluationValue.of(value, expressionConfiguration)
            } else {
                throw IllegalArgumentException("Invalid expression type for VarPlayer function")
            }
        }
    }

    @FunctionParameter(name = "key")
    class VarGlobal : AbstractFunction() {
        override fun evaluate(
            expression: Expression?,
            functionToken: Token?,
            vararg parameterValues: EvaluationValue?
        ): EvaluationValue {
            if (expression is PGExpression) {
                val key = parameterValues[0]?.stringValue ?: return EvaluationValue.NULL_VALUE
                val sandbox = expression.sandbox

                // Retrieve the variable from the global sandbox
                val value = sandbox.getVar(key)
                return EvaluationValue.of(value, expressionConfiguration)
            } else {
                throw IllegalArgumentException("Invalid expression type for VarGlobal function")
            }
        }
    }
}