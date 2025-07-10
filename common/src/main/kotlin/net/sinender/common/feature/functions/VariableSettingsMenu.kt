package net.sinender.common.feature.functions

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameterDefinition
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.managers.getSandbox
import net.sinender.common.menu.Backable
import net.sinender.common.menu.Menu
import net.sinender.common.menu.menuItem
import net.sinender.common.menu.menus.ConfirmMenu
import net.sinender.common.menu.menus.MaterialSelector
import net.sinender.utils.err
import net.sinender.utils.isValidIdentifier
import net.sinender.utils.openChat

class VariableSettingsMenu(private val variable: Pair<FunctionParameterDefinition, EvaluationValue.DataType>, val function: Function) : Menu(
    "Variable Settings",
    InventoryType.CHEST_4_ROW
), Backable {
    override fun setupItems(player: Player) {
        for (i in 0 until type.size) { //Template for adding items
            addItem(i, menuItem(Material.AIR) {
                player.sendMessage("You clicked on item $i")
            })
        }

        addItem(
            10, menuItem(Material.ANVIL) {
                player.openChat(function.name, "Enter new function name") { newName ->
                    if (newName.isBlank()) return@openChat player.err("Function name cannot be empty!")
                    if (!newName.isValidIdentifier()) return@openChat player.err("Function name is not a valid identifier!")
                    if (newName.length > 32) return@openChat player.err("Function name cannot be longer than 32 characters!")

                    function.name = newName
                    this.open(player)
                }
            }.name("<green>Function Name")
                .description("Edit the name of the function that is called with `fun.${function.name}(<arguments>)`\n\n<red>Note: You cannot have spaces in the function name!")
                .info("Current Name", "<gray>${function.name}")
                .action(ClickType.LEFT_CLICK, "to edit")
        )

        addItem(12,
            menuItem(Material.PISTON) {

            }.name("<green>Variables")
                .description("Edit the variables of the function.")
                .apply {
                    for (variable in function.variables) {
                        info("<gray>${variable.first.name}", "<white>${variable.second}")
                    }
                }
                .action(ClickType.LEFT_CLICK, "to edit")
        )

        addItem(
            14, menuItem(Material.BOOK) {
                player.openChat(function.name, "Enter new function name") { description ->
                    if (description.isBlank()) return@openChat player.err("Function name cannot be empty!")
                    function.description = description
                    this.open(player)
                }
            }.name("<green>Function Description")
                .description("Edit the description of the function.")
                .info("Current Description", "<gray>${function.description}")
                .action(ClickType.LEFT_CLICK, "to edit")
        )

        addItem(
            16, menuItem(function.icon) {
                MaterialSelector(this) { material ->
                    function.icon = material
                    this.open(player)
                }
            }.name("<green>Function Icon")
                .description("Edit the icon of the function.")
                .info("Current Icon", "<gray>${function.icon}")
                .action(ClickType.LEFT_CLICK, "to edit")
        )

        addItem(30, menuItem(Material.TNT) {
            ConfirmMenu(this) {
                player.getSandbox()?.functions?.remove(function) ?: return@ConfirmMenu player.err("You are not in a sandbox!")
                player.sendMessage("<red>Function <bold>${function.name}</bold> has been removed.")
            }
        }.name("<red>Remove Function")
            .description("Click to remove this function from the sandbox.")
            .action(ClickType.LEFT_CLICK, "to remove")
        )
    }

    override fun back(player: Player) {
        FunctionsMenu().open(player)
    }

    override fun backName(player: Player): String {
        return "Functions Menu"
    }
}