package net.sinender.common.action.actions

import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.item.Material
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.properties.DoubleProperty
import net.sinender.common.action.properties.EnumProperty
import net.sinender.common.action.properties.double
import net.sinender.common.action.properties.enum
import net.sinender.common.managers.Sandbox
import net.sinender.utils.EnumMaterial

class ChangeVelocity: Action(
    ActionEnum.CHANGE_VELOCITY,
    "Change Velocity",
    "Changes the velocity of the entity.",
    Material.SLIME_BALL
) {
    init {
        properties.addAll(listOf(
            EnumProperty(
                "direction",
                "Direction",
                "The direction to push the entity.",
                Material.ARROW,
                PushDirection::class.java,
                PushDirection.FORWARD
            ),
            EnumProperty(
                "operation",
                "Operation",
                "The operation to use when changing the velocity.",
                Material.COMPARATOR,
                VelocityOperation::class.java,
                VelocityOperation.SET
            ),
            DoubleProperty(
                "value",
                "Value",
                "The value to use when changing the velocity.",
                5.0,
            )
        ))
    }
    override fun execute(entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        if (entity is LivingEntity) {
            val direction = properties.enum<PushDirection>("direction") ?: return
            val operation = properties.enum<VelocityOperation>("operation") ?: return
            val value = (properties.double("value")  ?: return)

            val velocity = when (direction) {
                PushDirection.FORWARD -> entity.position.direction().mul(value)
                PushDirection.BACKWARD -> entity.position.direction().mul(-value)
                PushDirection.UP -> Vec(0.0, 1.0, 0.0).mul(value)
                PushDirection.DOWN -> Vec(0.0, -1.0, 0.0).mul(value)
                PushDirection.NORTH -> Vec(0.0, 0.0, -1.0).mul(value)
                PushDirection.SOUTH -> Vec(0.0, 0.0, 1.0).mul(value)
                PushDirection.EAST -> Vec(1.0, 0.0, 0.0).mul(value)
                PushDirection.WEST -> Vec(-1.0, 0.0, 0.0).mul(value)
                PushDirection.RIGHT -> {
                    val right = entity.position.direction().cross(Vec(0.0, 1.0, 0.0))
                    right.mul(value)
                }
                PushDirection.LEFT -> {
                    val left = entity.position.direction().cross(Vec(0.0, -1.0, 0.0))
                    left.mul(value)
                }
                else -> return
            }

            when (operation) {
                VelocityOperation.SET -> entity.velocity = velocity
                VelocityOperation.ADD -> entity.velocity = entity.velocity.add(velocity)
                VelocityOperation.SUBTRACT -> entity.velocity = entity.velocity.sub(velocity)
                VelocityOperation.MULTIPLY -> entity.velocity = entity.velocity.mul(velocity)
                VelocityOperation.DIVIDE -> entity.velocity = entity.velocity.div(velocity)
            }
        }
    }
}

enum class PushDirection(override val material: Material = Material.COMPASS) : EnumMaterial {
    FORWARD,
    BACKWARD,
    UP,
    DOWN,
    RIGHT,
    LEFT,
    NORTH,
    SOUTH,
    EAST,
    WEST,
    ;
}

enum class VelocityOperation(override val material: Material, val alternative: String) :
    EnumMaterial {
    SET(Material.YELLOW_STAINED_GLASS, "="),
    ADD(Material.GREEN_STAINED_GLASS, "+"),
    SUBTRACT(Material.RED_STAINED_GLASS, "-"),
    MULTIPLY(Material.ORANGE_STAINED_GLASS, "*"),
    DIVIDE(Material.BLUE_STAINED_GLASS, "/");

    fun asString(): String {
        return alternative
    }

    companion object {
        fun fromString(string: String?): VelocityOperation? {
            for (operation in entries) {
                if (operation.asString().equals(string, ignoreCase = true) || operation.name.equals(
                        string,
                        ignoreCase = true
                    )
                ) {
                    return operation
                }
            }
            return null
        }
    }
}
