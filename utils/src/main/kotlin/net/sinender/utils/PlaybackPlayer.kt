package net.sinender.utils

import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Metadata
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Property
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Entry

class PlaybackPlayer(
    private val username: String,
    private val skinTexture: String?,
    private val skinSignature: String?
) : EntityCreature(EntityType.PLAYER) {

    init {
        setNoGravity(true)
    }

    override fun updateNewViewer(player: Player) {
        val properties = mutableListOf<Property>()
        if (skinTexture != null && skinSignature != null) {
            properties.add(Property("textures", skinTexture, skinSignature))
        }
        val entry = Entry(
            uuid,
            username,
            properties,
            false,
            0,
            GameMode.SURVIVAL,
            null,
            null,
            1
        )
        player.sendPacket(PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry))

        // Spawn the player entity
        super.updateNewViewer(player)

        // Enable skin layers
        player.sendPackets(EntityMetaDataPacket(entityId, mapOf(17 to Metadata.Byte(127.toByte()))))
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        player.sendPacket(PlayerInfoRemovePacket(uuid))
    }
}