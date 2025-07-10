package net.sinender.common.feature.evalex

import com.ezylang.evalex.Expression
import com.ezylang.evalex.config.ExpressionConfiguration
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.sinender.common.postgres.Sandbox


val expressionConfiguration: ExpressionConfiguration = ExpressionConfiguration.builder()
    .structuresAllowed(false)
    .build().apply {
        VariableFunctions.apply(this.functionDictionary)
    }



class PGExpression(expressionString: String?, val entity: Entity?, val player: Player?, val sandbox: Sandbox, val event: Event?) : Expression(expressionString, expressionConfiguration.apply {
    sandbox.functions.forEach { function ->
        this.functionDictionary.addFunction("fun.${function.name}", function.evalFunction)
    }
}) {
    init {
        this.withValues(mutableMapOf(
            "player.name" to player?.username,
            "player.uuid" to player?.uuid.toString(),
            "player.ping" to player?.latency,
            "player.pos.x" to player?.position?.x,
            "player.pos.y" to player?.position?.y,
            "player.pos.z" to player?.position?.z,
            "player.pos.yaw" to player?.position?.yaw,
            "player.pos.pitch" to player?.position?.pitch,
        ))
    }

    override fun withValues(values: MutableMap<String, *>?): PGExpression {
        return super.withValues(values) as PGExpression
    }
}