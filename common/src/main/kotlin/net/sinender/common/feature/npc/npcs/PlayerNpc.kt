package net.sinender.common.feature.npc.npcs

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.PlayerSkin
import net.sinender.common.action.properties.StringProperty
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.managers.Sandbox
import net.sinender.utils.PlaybackPlayer


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

    override fun spawn(sandbox: Sandbox) {
        val skinFromUUID = PlayerSkin.fromUuid(properties[0].value as String) ?:
        PlayerSkin.fromUsername(name)?: return
        val player = PlaybackPlayer(
            name,
            skinFromUUID.textures(),
            skinFromUUID.signature()
        )

        player.setInstance(sandbox.instance!!, position)
        initListenerAndScheduler(player, sandbox)
    }
}