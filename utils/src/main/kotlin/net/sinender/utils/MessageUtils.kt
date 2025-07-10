package net.sinender.utils

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import revxrsal.commands.minestom.actor.MinestomCommandActor

fun MinestomCommandActor.send(message: String): Component {
    return this.asPlayer()!!.send(message)
}

fun Player.send(message: String): Component {
    val msg: Component = colorize(message)
    this.sendMessage(msg)
    return msg
}

fun MinestomCommandActor.info(message: String): Component {
    return this.asPlayer()!!.info(message)
}

fun Player.info(message: String): Component {
    checkNotNull(primaryColor)
    val msg = colorize(
        "<color:" + brightness(
            primaryColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">ℹ</color> <primary>" + message
    )
    this.sendMessage(msg)
    return msg
}

fun Player.success(message: String): Component {
    checkNotNull(successColor)
    val msg = colorize(
        "<color:" + brightness(
            successColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">⭐</color> <success>" + message
    )
    this.sendMessage(msg)
    return msg
}

fun MinestomCommandActor.success(message: String): Component {
    return this.asPlayer()!!.success(message)
}

fun Player.warning(message: String): Component {
    checkNotNull(warningColor)
    val msg = colorize(
        "<color:" + brightness(
            warningColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">⚠</color> <warning>" + message
    )
    this.sendMessage(msg)
    return msg
}

fun MinestomCommandActor.warning(message: String): Component {
    return this.asPlayer()!!.warning(message)
}

fun Player.err(message: String) {
    checkNotNull(errorColor)
    val msg = colorize(
        "<color:" + brightness(
            errorColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">❌</color> <error>" + message
    )
    this.sendMessage(msg)
}

fun MinestomCommandActor.err(message: String) {
    this.asPlayer()!!.err(message)
}