package net.sinender.common.feature.functions

import com.ezylang.evalex.data.EvaluationValue
import com.ezylang.evalex.functions.FunctionParameterDefinition
import feature.actionMenu.ActionsMenu
import net.minestom.server.entity.Player
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.inventory.click.Click.Right
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.Material
import net.sinender.common.action.actions.Return
import net.sinender.common.feature.housingMenu.SystemsMenu
import net.sinender.common.managers.getSandbox
import net.sinender.common.menu.*
import net.sinender.common.menu.items.BackItem
import net.sinender.utils.PaginationList
import net.sinender.utils.err
import net.sinender.utils.isValidIdentifier
import net.sinender.utils.openChat

class VariablesMenu(val function: Function) : PaginationMenu(
    "Variables",
    InventoryType.CHEST_5_ROW
), Backable {
    override fun setupItems(player: Player) {
        super.setupItems(player)

        val sandbox = player.getSandbox() ?: return player.err("You are not in a sandbox!")

        addItem(
            53, menuItem(Material.LIME_WOOL) {
                player.openChat("", "Enter variable name") { name ->
                    if (name.isBlank()) return@openChat player.err("Variable name cannot be empty!")
                    if (!name.isValidIdentifier()) return@openChat player.err("Variable name is not a valid identifier!")
                    if (name.length > 16) return@openChat player.err("Variable name cannot be longer than 16 characters!")

                    if (sandbox.functions.any { it.variables.any { it.first.name == name } }) {
                        return@openChat player.err("Variable with name '$name' already exists in this function!")
                    }

                    val parameterDefinition = FunctionParameterDefinition.builder().name(name).build()
                    function.variables.add(Pair(parameterDefinition, EvaluationValue.DataType.STRING))

                    this.open(player)
                }
            }.name("<green>Add Variable")
                .description("Click to add a new variable")
                .action(ClickType.LEFT_CLICK, "to add")
        )
    }

    override fun paginationList(player: Player): PaginationList<MenuItem>? {
        val sandbox = player.getSandbox() ?: return null
        return PaginationList(sandbox.functions.map { function ->
            menuItem(function.icon) {
                ActionsMenu(function.actions, this).open(player)
            }.name("<green>${function.name}")
                .description(function.description)
                .info("Actions", "<gray>${function.actions.size} actions")
                .info(null, "")
                .info("<yellow>Variables")
                .apply {
                    function.variables.forEach {
                        this.info("<gray>${it.first.name}", "<white>${it.second}")
                    }
                }
                .action(ClickType.LEFT_CLICK, "to edit actions")
                .action(ClickType.RIGHT_CLICK, "to edit settings")
        }, slots.size)
    }

    override fun back(player: Player) {
        FunctionSettingsMenu(function).open(player)
    }

    override fun backName(player: Player): String {
        return "Function Settings Menu"
    }
}