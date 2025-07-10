package net.sinender.common.feature.functions

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameter
import com.ezylang.evalex.functions.FunctionParameterDefinition
import com.ezylang.evalex.functions.FunctionParameterDefinition.*
import com.ezylang.evalex.parser.Token
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionExecutor
import net.sinender.common.feature.evalex.AbstractFunction
import net.sinender.common.feature.evalex.PGExpression

class Function(
    var name: String = "",
    var description: String = "",
    var icon: Material = Material.MAP,
    val actions: MutableList<Action> = mutableListOf(),
    variables: MutableList<Pair<FunctionParameterDefinitionBuilder, EvaluationValue.DataType>> = mutableListOf()
) {
    val variables: MutableList<Pair<FunctionParameterDefinition, EvaluationValue.DataType>> = variables.map {
        Pair(it.first.build(), it.second)
    }.toMutableList()
    val evalFunction = EvalFunction(this.variables, this)

    fun evaluate(
        expression: PGExpression?,
        token: Token?,
        vararg parameterValues: EvaluationValue?
    ): EvaluationValue? {
        val event = FunctionEvent(this)
        val executor = ActionExecutor(
            expression?.entity ?: return EvaluationValue.NULL_VALUE,
            expression.player,
            expression.sandbox,
            event,
            actions,
        ).execute {
            PGExpression(it, expression.entity, expression.player, expression.sandbox, event).withValues(
                hashMapOf(
                    *this.variables.mapIndexed { index, it ->
                        Pair(it.first.name, parameterValues[index])
                    }.toTypedArray()
                )
            )
        }

        return executor
    }
}

class EvalFunction(
    variables: MutableList<Pair<FunctionParameterDefinition, EvaluationValue.DataType>>,
    private val func: Function
) : AbstractFunction() {
    init {
        for ((def, type) in variables) {
            functionParameterDefinitions.add(def)
        }
    }

    override fun evaluate(
        expression: PGExpression?,
        token: Token?,
        vararg parameterValues: EvaluationValue?
    ): EvaluationValue {
        return func.evaluate(expression, token, *parameterValues) ?: EvaluationValue.NULL_VALUE
    }
}