package net.sinender.utils

import net.kyori.adventure.text.Component
import revxrsal.commands.minestom.actor.MinestomCommandActor

fun MinestomCommandActor.send(message: String): Component {
    val msg: Component = colorize(message)
    this.sendRawMessage(msg)
    return msg
}

fun MinestomCommandActor.info(message: String): Component {
    checkNotNull(primaryColor)
    val msg = colorize(
        "<color:" + brightness(
            primaryColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">ℹ</color> <primary>" + message
    )
    this.sendRawMessage(msg)
    return msg
}

fun info(message: String): String {
    checkNotNull(primaryColor)
    return "<color:" + brightness(primaryColor, ICON_BRIGHTNESS_FACTOR).asHexString() + ">ℹ</color> <primary>" + message
}

fun MinestomCommandActor.success(message: String): Component {
    checkNotNull(successColor)
    val msg = colorize(
        "<color:" + brightness(
            successColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">⭐</color> <success>" + message
    )
    this.sendRawMessage(msg)
    return msg
}

fun MinestomCommandActor.warning(message: String): Component {
    checkNotNull(warningColor)
    val msg = colorize(
        "<color:" + brightness(
            warningColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">⚠</color> <warning>" + message
    )
    this.sendRawMessage(msg)
    return msg
}

fun MinestomCommandActor.err(message: String): Component {
    checkNotNull(errorColor)
    val msg = colorize(
        "<color:" + brightness(
            errorColor,
            ICON_BRIGHTNESS_FACTOR
        ).asHexString() + ">❌</color> <error>" + message
    )
    this.sendRawMessage(msg)
    return msg
}