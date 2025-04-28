package net.sinender.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.*
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Entry
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.Property
import net.minestom.server.scoreboard.Team


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
            removeColor(username),
            properties,
            false,
            0,
            GameMode.SURVIVAL,
            Component.text("Minestom", TextColor.color(0xff6c33)),
            null,
            1
        )
        player.sendPacket(PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry))
        setNoGravity(true)
        // Spawn the player entity
        super.updateNewViewer(player)
        // Enable skin layers
        player.sendPackets(EntityMetaDataPacket(entityId, mapOf(
            3 to Metadata.Boolean(true),
        )))
    }

    override fun updateOldViewer(player: Player) {
        super.updateOldViewer(player)
        player.sendPacket(PlayerInfoRemovePacket(uuid))
    }
}