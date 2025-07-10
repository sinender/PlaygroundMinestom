package net.sinender.common.feature.npc.npcs

import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.component.HeadProfile
import net.minestom.server.network.packet.server.play.TeamsPacket
import net.minestom.server.scoreboard.Team
import net.minestom.server.scoreboard.TeamBuilder
import net.sinender.common.action.Description
import net.sinender.common.action.DisplayName
import net.sinender.common.action.properties.StringPropertyAnnotation
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.menu.MenuItem
import net.sinender.common.postgres.Sandbox
import net.sinender.utils.PlaybackPlayer
import net.sinender.utils.removeColor


val team = TeamBuilder("NPCS", MinecraftServer.getTeamManager())
    .teamColor(NamedTextColor.GREEN)
    .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
    .build()

class PlayerNpc(
    id: Int = -1,
    position: Pos = Pos(0.0, 0.0, 0.0)
) : NpcEntity(
    id,
    position,
    EntityType.PLAYER
) {
    @DisplayName("Skin") @Description("The uuid of the player skin to use.")
    @StringPropertyAnnotation
    var skin: String = "e54a1fdb-03e1-4304-91b4-a92b3015dbd9"

    override fun onSpawn(entityCreature: EntityCreature, sandbox: Sandbox) {
        team.addMember(entityCreature.uuid.toString())

        team.sendUpdatePacket()
    }

    override fun distinctMenuItem(): MenuItem {
        val skinFromUUID =
            PlayerSkin.fromUuid(skin) ?: PlayerSkin.fromUsername(removeColor(name)) ?: return super.distinctMenuItem()
        return super.distinctMenuItem()
            .itemStack(
                ItemStack.of(Material.PLAYER_HEAD)
                    .with(DataComponents.PROFILE, HeadProfile(skinFromUUID))
            )
    }

    override fun spawn(sandbox: Sandbox) {
        val skinFromUUID =
            PlayerSkin.fromUuid(skin) ?: PlayerSkin.fromUsername(removeColor(name)) ?: return
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