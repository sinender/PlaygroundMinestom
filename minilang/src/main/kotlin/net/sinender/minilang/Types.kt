package net.sinender.minilang

enum class DataType {
    INTEGER, DOUBLE, STRING, BOOLEAN, NULL
}

sealed class ExpressionValue(val type: DataType) {
    abstract fun toDisplayString(): String
    open fun isTruthy(): Boolean = true

    data class IntegerValue(val value: Int): ExpressionValue(DataType.INTEGER) {
        override fun toDisplayString(): String = value.toString()
        override fun isTruthy(): Boolean = value != 0
    }

    data class DoubleValue(val value: Double): ExpressionValue(DataType.DOUBLE) {
        override fun toDisplayString(): String = value.toString()
        override fun isTruthy(): Boolean = value != 0.0
    }

    data class StringValue(val value: String): ExpressionValue(DataType.STRING) {
        override fun toDisplayString(): String = value
        override fun isTruthy(): Boolean = value.isNotEmpty()
    }

    data class BooleanValue(val value: Boolean): ExpressionValue(DataType.BOOLEAN) {
        override fun toDisplayString(): String = value.toString()
        override fun isTruthy(): Boolean = value
    }

    data object NullValue: ExpressionValue(DataType.NULL) {
        override fun toDisplayString(): String = "null"
        override fun isTruthy(): Boolean = false
    }

    fun asInt(): Int? = (this as? IntegerValue)?.value
    fun asDouble(): Double? = (this as? DoubleValue)?.value
    fun asString(): String? = (this as? StringValue)?.value
    fun asBoolean(): Boolean? = (this as? BooleanValue)?.value

    fun toNumeric(): ExpressionValue? {
        return when (this) {
            is IntegerValue -> this
            is DoubleValue -> this
            is StringValue -> {
                this.value.toIntOrNull()?.let { IntegerValue(it) }
                    ?: this.value.toDoubleOrNull()?.let { DoubleValue(it) }
            }
            else -> null
        }
    }
}

object TypeInferrer {
    fun inferType(input: String): ExpressionValue {
        return when {
            input == "null" -> ExpressionValue.NullValue
            input == "true" -> ExpressionValue.BooleanValue(true)
            input == "false" -> ExpressionValue.BooleanValue(false)
            input.toIntOrNull() != null -> ExpressionValue.IntegerValue(input.toInt())
            input.toDoubleOrNull() != null -> ExpressionValue.DoubleValue(input.toDouble())
            else -> ExpressionValue.StringValue(input)
        }
    }
}

fun main() {
    println(TypeInferrer.inferType("5"))           // IntegerValue(value=5)
    println(TypeInferrer.inferType("15.3"))        // DoubleValue(value=15.3)
    println(TypeInferrer.inferType("true"))        // BooleanValue(value=true)
    println(TypeInferrer.inferType("null"))        // NullValue
    println(TypeInferrer.inferType("Test string")) // StringValue(value=Test string)
    println(TypeInferrer.inferType("123FakeSt"))   // StringValue(value=123FakeSt)
    println(TypeInferrer.inferType("player.pos.x"))// StringValue(value=player.pos.x) - This will be parsed as a string initially
}