package net.sinender.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor.DARK_RED
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import java.util.*
import java.util.Map.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.min


val primaryColor = TextColor.fromHexString("#59bdff")
val secondaryColor = TextColor.fromHexString("#6696e3")
val successColor = TextColor.fromHexString("#81e366")
val warningColor = TextColor.fromHexString("#e35b5e")
val errorColor = TextColor.fromHexString("#ad231f")

const val ICON_BRIGHTNESS_FACTOR = 1.5

// Format minimessage string using Adventure API
fun colorize(message: String): Component {
    assert(primaryColor != null && secondaryColor != null && successColor != null && warningColor != null && errorColor != null) { "One or more colors are not initialized" }
    return MiniMessage.miniMessage().deserialize(
        "<reset><!i>$message",
        Placeholder.styling("primary", primaryColor!!),
        Placeholder.styling("secondary", secondaryColor!!),
        Placeholder.styling("success", successColor!!),
        Placeholder.styling("warning", warningColor!!),
        Placeholder.styling("error", errorColor!!)
    )
}

fun brightness(color: TextColor, factor: Double): TextColor {
    val red = min(255.0, color.red() * factor).toInt()
    val green = min(255.0, color.green() * factor).toInt()
    val blue = min(255.0, color.blue() * factor).toInt()
    return TextColor.color(red, green, blue)
}

val unicodeFontMap: Map<Char, Char> = HashMap(
    ofEntries(
        entry('a', 'ᴀ'), entry('b', 'ʙ'), entry('c', 'ᴄ'), entry('d', 'ᴅ'), entry('e', 'ᴇ'),
        entry('f', 'ꜰ'), entry('g', 'ɢ'), entry('h', 'ʜ'), entry('i', 'ɪ'), entry('j', 'ᴊ'),
        entry('k', 'ᴋ'), entry('l', 'ʟ'), entry('m', 'ᴍ'), entry('n', 'ɴ'), entry('o', 'ᴏ'),
        entry('p', 'ᴘ'), entry('q', 'ꞯ'), entry('r', 'ʀ'), entry('s', 'ꜱ'), entry('t', 'ᴛ'),
        entry('u', 'ᴜ'), entry('v', 'ᴠ'), entry('w', 'ᴡ'), entry('x', 'x'), entry('y', 'ʏ'),
        entry('z', 'ᴢ')
    )
)

fun smallCapsFontNormalCaps(input: String): String {
    val output = StringBuilder()
    for (c in input.toCharArray()) {
        output.append(unicodeFontMap.getOrDefault(c.uppercaseChar(), c))
    }
    return output.toString()
}

fun smallCapsFont(input: String): String {
    val output = StringBuilder()
    for (c in input.toCharArray()) {
        output.append(unicodeFontMap.getOrDefault(c, c))
    }
    return output.toString()
}

val UNSUPPORTED: TextComponent = text("ERROR WRAPPING").color(DARK_RED)

fun wrapLoreLines(component: Component, length: Int): List<Component> {
    if (component !is TextComponent) {
        return Collections.singletonList(component)
    }

    val wrapped: MutableList<Component> = ArrayList()
    val parts: List<TextComponent> = flattenTextComponents(component)

    var currentLine: Component = empty()
    var lineLength = 0

    for (part in parts) {
        val style: Style = part.style()
        val content: String = part.content()
        val words = content.split("(?<=\\s)|(?=\\n)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (word in words) {
            if (word.isEmpty()) {
                continue
            }

            val wordLength = word.length
            val totalLength = lineLength + wordLength
            if (totalLength > length || word.contains("\n")) {
                wrapped.add(currentLine)
                currentLine = empty().style(style)
                lineLength = 0
            }

            if (word != "\n") {
                currentLine = currentLine.append(text(word).style(style))
                lineLength += wordLength
            }
        }
    }

    if (lineLength > 0) {
        wrapped.add(currentLine)
    }

    return wrapped
}

private fun flattenTextComponents(component: TextComponent): List<TextComponent> {
    val flattened: MutableList<TextComponent> = ArrayList<TextComponent>()
    val style: Style = component.style()
    val enforcedState: Style = enforceStyleStates(style)
    val first: TextComponent = component.style(enforcedState)

    val toCheck: Stack<TextComponent> = Stack()
    toCheck.add(first)

    while (!toCheck.empty()) {
        val parent: TextComponent = toCheck.pop()
        val content: String = parent.content()
        if (!content.isEmpty()) {
            flattened.add(parent)
        }

        val children: List<Component> = parent.children()
        val reversed = children.reversed()
        for (child in reversed) {
            if (child is TextComponent) {
                val parentStyle: Style = parent.style()
                val textStyle: Style = child.style()
                val merge: Style = parentStyle.merge(textStyle)
                val childComponent: TextComponent = child.style(merge)
                toCheck.add(childComponent)
            } else {
                toCheck.add(UNSUPPORTED)
            }
        }
    }
    return flattened
}

private fun enforceStyleStates(style: Style): Style {
    val builder: Style.Builder = style.toBuilder()
    val map: Map<TextDecoration, TextDecoration.State> = style.decorations()
    map.forEach { (decoration: TextDecoration, state: TextDecoration.State) ->
        if (state === TextDecoration.State.NOT_SET) {
            builder.decoration(decoration, false)
        }
    }
    return builder.build()
}