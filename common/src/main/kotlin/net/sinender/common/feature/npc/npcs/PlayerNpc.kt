package net.sinender.common.feature.npc.npcs

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.entity.metadata.PlayerMeta
import net.minestom.server.item.ItemComponent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.HeadProfile
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.sinender.common.action.properties.StringProperty
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.managers.Sandbox
import net.sinender.common.menu.MenuItem
import net.sinender.utils.NametagEntity
import net.sinender.utils.PlaybackPlayer
import net.sinender.utils.removeColor


class PlayerNpc(
    id: Int = -1,
    position: Pos = Pos(0.0, 0.0, 0.0)
) : NpcEntity(
    id,
    position,
    EntityType.PLAYER
) {
    init {
        properties.add(
            StringProperty(
                "skin",
                "Player UUID",
                "The uuid of the player to use as skin",
                "e54a1fdb-03e1-4304-91b4-a92b3015dbd9",
            )
        )
    }

    override fun onSpawn(entityCreature: EntityCreature, sandbox: Sandbox) {

    }

    override fun distinctMenuItem(): MenuItem {
        val skinFromUUID =
            PlayerSkin.fromUuid(properties[0].value as String) ?: PlayerSkin.fromUsername(removeColor(name)) ?: return super.distinctMenuItem()
        return super.distinctMenuItem()
            .itemStack(
                ItemStack.of(Material.PLAYER_HEAD)
                    .with(ItemComponent.PROFILE, HeadProfile(skinFromUUID))
            )
    }

    override fun spawn(sandbox: Sandbox) {
        val skinFromUUID =
            PlayerSkin.fromUuid(properties[0].value as String) ?: PlayerSkin.fromUsername(removeColor(name)) ?: return
        val player = PlaybackPlayer(
            name,
            skinFromUUID.textures(),
            skinFromUUID.signature()
        )

        player.setInstance(sandbox.instance!!, position)
        initListenerAndScheduler(player, sandbox)

//        NametagEntity(player)
    }
}