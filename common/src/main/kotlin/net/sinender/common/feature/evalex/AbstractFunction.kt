package net.sinender.common.feature.evalex

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.AbstractFunction
import com.ezylang.evalex.parser.Token

abstract class AbstractFunction: AbstractFunction() {
    abstract fun evaluate(expression: PGExpression?, token: Token?, vararg parameterValues: EvaluationValue?): EvaluationValue

    override fun evaluate(expression: Expression?, token: Token?, vararg parameterValues: EvaluationValue?): EvaluationValue {
        if (expression is PGExpression) {
            return evaluate(expression, token, *parameterValues)
        } else {
            throw IllegalArgumentException("Invalid expression type for " + this::class.java.simpleName + " function")
        }
    }
}