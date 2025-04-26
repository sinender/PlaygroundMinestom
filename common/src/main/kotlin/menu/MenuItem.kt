package menu

import formatCapitalize
import net.kyori.adventure.text.Component
import net.minestom.server.event.inventory.InventoryClickEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.CustomModelData
import net.minestom.server.item.component.EnchantmentList
import net.minestom.server.item.enchant.Enchantment
import net.sinender.utils.colorize
import net.sinender.utils.wrapLoreLines

class MenuItem (
    var item: ItemStack,
    var action: (event: InventoryPreClickEvent) -> Unit
){
    private var name: String? = null
    private var description: String? = null
    private var extraLore = mutableListOf<String>()
    private var actions = mapOf<ClickType, String>()
    private var info = mapOf<String?, String>()
    private var glow = false
    private var changeOrder = false
    private var punctuation = false
    private var textWidth = 40
    private var itemModel: String? = null

    fun name(name: String): MenuItem {
        this.name = name
        return this
    }

    fun description(description: String?): MenuItem {
        this.description = description
        return this
    }

    fun extraLore(vararg lore: String): MenuItem {
        extraLore.addAll(lore)
        return this
    }

    fun action(clickType: ClickType, action: String): MenuItem {
        actions = actions + (clickType to action)
        return this
    }

    fun info(key: String?, value: String): MenuItem {
        info = info + (key to value)
        return this
    }

    fun glow(glow: Boolean): MenuItem {
        this.glow = glow
        return this
    }

    fun changeOrder(changeOrder: Boolean): MenuItem {
        this.changeOrder = changeOrder
        return this
    }

    fun punctuation(punctuation: Boolean): MenuItem {
        this.punctuation = punctuation
        return this
    }

    fun textWidth(textWidth: Int): MenuItem {
        this.textWidth = textWidth
        return this
    }

    fun leftClick(action: String): MenuItem {
        return action(ClickType.LEFT_CLICK, action)
    }

    fun rightClick(action: String): MenuItem {
        return action(ClickType.RIGHT_CLICK, action)
    }

    fun itemModel(itemModel: String): MenuItem {
        this.itemModel = itemModel
        return this
    }

    fun build(): ItemStack {
        var item = item.with {
            if (name != null) it.customName(colorize("<green>$name</green>"))
        }

        //If the item has a lore, we add the description to it
        val lore = item.get(ItemComponent.LORE)?.toMutableList() ?: mutableListOf()
        if (description != null) {
            lore.addAll(wrapLoreLines(colorize("<gray>$description</gray>"), textWidth))
        }

        if (info.isNotEmpty()) {
            lore.add(colorize(""))
            info.forEach { (key, value) ->
                lore.add(colorize("${if (key != null) "<white>$key:</white> " else ""}<green>$value</green>"))
            }
        }
        if (actions.isNotEmpty()) {
            lore.add(colorize(""))
            actions.forEach { (clickType, action) ->
                lore.add(colorize("<yellow>${clickType.name.formatCapitalize()} $action!</yellow>"))
            }
        }
        extraLore.forEach {
            lore.add(colorize(it))
        }

        item = item.with {
            it.lore(lore)
            it.glowing(glow)
            if (itemModel != null) it.itemModel(itemModel!!)
            it.hideExtraTooltip()
        }
        return item
    }

    fun clone(): MenuItem {
        val clonedItem = item.with {}
        val clonedMenuItem = MenuItem(clonedItem, action)
        clonedMenuItem.name = name
        clonedMenuItem.description = description
        clonedMenuItem.extraLore = extraLore.toMutableList()
        clonedMenuItem.actions = actions
        clonedMenuItem.info = info
        clonedMenuItem.glow = glow
        clonedMenuItem.changeOrder = changeOrder
        clonedMenuItem.punctuation = punctuation
        clonedMenuItem.textWidth = textWidth
        return clonedMenuItem
    }
}

fun menuItem(item: ItemStack, action: (event: InventoryPreClickEvent) -> Unit) = MenuItem(item, action)
fun menuItem(material: Material, action: (event: InventoryPreClickEvent) -> Unit) = MenuItem(ItemStack.of(material), action)