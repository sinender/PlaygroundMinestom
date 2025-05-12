package net.sinender.utils

import net.minestom.server.entity.metadata.other.InteractionMeta
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket
import net.minestom.server.tag.Tag
import net.kyori.adventure.text.Component
import net.minestom.server.entity.*

class NametagEntity(player: PlaybackPlayer) : Entity(EntityType.INTERACTION) {

    companion object {
        val NAMETAG_TAG: Tag<NametagEntity> = Tag.Transient("nametag") // for deleting the entity on leave
    }

    init {
        val meta = this.entityMeta as InteractionMeta
        meta.isCustomNameVisible = true
        meta.customName = player.customName ?: Component.text("you should probably set this to something...")

        // This isn't perfect, hopefully one day when we can get the offsets it'll be.
        meta.height = 0.1F
        meta.width = 0.1F

        // Set the pose to update the height.
        meta.pose = EntityPose.SNIFFING

        player.setTag(NAMETAG_TAG, this)
        this.setInstance(player.instance, player.position)

        player.addPassenger(this)


    }

    override fun updateNewViewer(player: Player) {
        super.updateNewViewer(player)

        // A tick after the initial meta is sent, send a new meta packet with a large height to stop it from disappearing.
        this.scheduler().scheduleNextTick {
            if (player.isDead) return@scheduleNextTick
            player.sendPacket(EntityMetaDataPacket(this.entityId, mapOf(9 to Metadata.Float(99999999f))))
        }
    }
}