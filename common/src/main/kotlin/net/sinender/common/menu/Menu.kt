package net.sinender.common.menu

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryClickEvent
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.event.trait.InventoryEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.sinender.utils.colorize

abstract class Menu(
    val title: String, //Translated title
    val type: InventoryType,
) {

    protected var items = mutableMapOf<Int, MenuItem>()
    protected val slots: List<Int>

    init {
        slots = mutableListOf<Int>()
        for (i in 1 until (type.size / 9) - 2) {
            repeat(7) { j ->
                slots.add(i * 9 + 1 + j)
            }
        }
    }

    protected val center get() = ((type.size / 9) / 2) * 9 - 5

    abstract fun setupItems(player: Player)
    fun getItem(slot: Int): MenuItem? = items[slot]
    fun addItem(slot: Int, item: MenuItem) {
        items[slot] = item
    }

    //Can be used for pagination menus
    fun addItem(item: MenuItem) {
        for (i in 0 until type.size) {
            if (items[i] == null) {
                items[i] = item
                return
            }
        }
    }

    fun slot(row: Int, column: Int): Int {
        return (row * 9) + column
    }

    fun clearItems(player: Player) {
        items.clear()

        for (i in 0 until type.size - 1) {
            player.openInventory?.setItemStack(i, ItemStack.AIR)
        }
    }

    open fun updateItems(player: Player) {
        clearItems(player)

        setupItems(player)
        items.forEach { (slot, item) ->
            player.openInventory?.setItemStack(slot, item.build())
        }
    }

    fun open(player: Player) {
        open(player, null)
    }

    open fun open(player: Player, comp: Component?) {

        val inventory = Inventory(type, comp ?: colorize(title))
        player.openInventory(inventory)
        setupItems(player)

        if (this is Backable && (items[type.size - 5] == null || items[type.size - 5]!!.item.isAir)) {
            val backable = this as Backable
            addItem(type.size - 5, menuItem(Material.ARROW) {
                backable.back(player)
            }.name("<red>Go Back").description("To ${backable.backName(player)}"))
        }

        items.forEach { (slot, item) ->
            inventory.setItemStack(slot, item.build())
        }

//        player.openInventory(inventory)

        var handler = MinecraftServer.getGlobalEventHandler();
        var node: EventNode<InventoryEvent>? = null;
        node = EventNode.type("click", EventFilter.INVENTORY) { _, inv ->
            inv == inventory
        }.addListener(InventoryPreClickEvent::class.java) { event ->
            event.isCancelled = true
            val item = getItem(event.slot)
            item?.action?.invoke(event)
        }.addListener(InventoryCloseEvent::class.java) { event ->
            if (node != null) handler.removeChild(node!!)
        }

        handler.addChild(node)
    }

    fun close(player: Player) {
        player.closeInventory()
    }
}