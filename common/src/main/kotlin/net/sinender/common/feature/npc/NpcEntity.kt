package net.sinender.common.feature.npc

import io.github.togar2.pvp.events.PrepareAttackEvent
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.ai.TargetSelector
import net.minestom.server.event.Event
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.item.Material
import net.minestom.server.timer.TaskSchedule
import net.sinender.common.action.*
import net.sinender.common.action.properties.BooleanPropertyAnnotation
import net.sinender.common.feature.npc.events.NpcEventType
import net.sinender.common.feature.npc.menu.NpcEditMenu
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.menuItem
import net.sinender.common.postgres.Sandbox
import net.sinender.common.utils.susListener
import org.reflections.Reflections
import java.lang.reflect.Field
import kotlin.random.Random
import kotlin.reflect.KClass

abstract class NpcEntity(
    var id: Int = -1,
    var position: Pos = Pos(0.0, 0.0, 0.0),
    var entityType: EntityType = EntityType.PLAYER,
) {
    companion object {
        val entries = mutableMapOf<String, Class<out NpcEntity>>()

        init {
            val reflections = Reflections("net.sinender.common.feature.npc.npcs")
            val npcClasses = reflections.getSubTypesOf(NpcEntity::class.java)
            for (propertyClass in npcClasses) {
                val npcEntity = propertyClass.getDeclaredConstructor().newInstance()
                entries[npcEntity.entityType.name()] = propertyClass
            }
        }

        fun getNpcEntity(type: String): Class<out NpcEntity>? {
            return entries[type]
        }

        fun getCurrentID(sandbox: Sandbox): Int {
            return sandbox.npcs.size + 1
        }
    }

    var spawned = false
    var name: String = listOf(
        "<green>Alex",
        "<dark_red>Baldrick",
        "<rainbow>Diddy",
        "<dark_purple>Ben Dover",
        "<gray>Loading...",
        "<yellow>Updog",
        "<aqua>Cookie Monster",
        "<red>❤"
    )[Random.nextInt(8)]
    var eventActions = mutableMapOf<NpcEventType, MutableList<Action>>()

    val properties: List<Pair<Field, ActionProperty<*>>>
        get() {
            val properties = mutableListOf<Pair<Field, ActionProperty<*>>>()
            var fields = this.javaClass.declaredFields
            if (this.javaClass.superclass != null) {
                fields = this.javaClass.superclass.declaredFields + fields
            }
            for (field in fields) {
                for (property in ActionProperties.entries) {
                    if (field.isAnnotationPresent(property.annotation.java)) {
                        properties.add(Pair(field, property))
                    }
                }
            }
            return properties
        }

    @DisplayName("Look At Player")
    @Description("Whether the NPC should look at the player.")
    @BooleanPropertyAnnotation
    var lookAtPlayer = true

    init {
        eventActions = NpcEventType.entries.associateWith {
            mutableListOf<Action>()
        }.toMutableMap()
    }

    open fun distinctMenuItem(): MenuItem {
        var item = menuItem(Material.SKELETON_SKULL)
            .name("<yellow>$name")

        item.info("<yellow>Properties")

        for (field in this.javaClass.declaredFields) {
            val displayName = field.getAnnotation(DisplayName::class.java)
            if (displayName != null) {
                item.info(displayName.value, field.displayValue(this))
            }
        }

        return item
    }

    abstract fun onSpawn(entityCreature: EntityCreature, sandbox: Sandbox)
    open fun startAI(entityCreature: EntityCreature): Pair<List<GoalSelector>, List<TargetSelector>> {
        // Default AI behavior
        return Pair(listOf(), listOf())
    }

    open fun spawn(sandbox: Sandbox) {
        val entity = EntityCreature(entityType);
        val pair = startAI(entity)
        entity.addAIGroup(pair.first, pair.second)
        entity.setInstance(sandbox.instance!!, position);
        spawned = true
        initListenerAndScheduler(entity, sandbox)
    }

    @Transient
    val cooldowns = mutableMapOf<String, HashMap<String, Long>>()
    protected fun initListenerAndScheduler(entity: EntityCreature, sandbox: Sandbox) {
        MinecraftServer.getSchedulerManager().submitTask {
            if (lookAtPlayer) {
                sandbox.instance!!.players.minByOrNull {
                    it.position.distance(entity.position)
                }?.let { player ->
                    if (player.position.distance(entity.position) > 5) return@let
                    entity.lookAt(player.position.add(0.0, player.eyeHeight, 0.0))
                }
            }
            if (entity.position.y < -64) {
                entity.teleport(position)
            }

            if (entity.isDead) {
                if (cooldowns.containsKey("npc")) {
                    if (System.currentTimeMillis() - cooldowns["npc"]!!["death"]!! >= 1000) {
                        entity.remove()
                        spawn(sandbox)
                        cooldowns.remove("npc")
                        return@submitTask TaskSchedule.stop()
                    }
                } else {
                    cooldowns["npc"] = HashMap()
                    cooldowns["npc"]!!["death"] = System.currentTimeMillis()
                }
            }

            TaskSchedule.tick(1);
        }

        var node = EventNode.type("npc-event", EventFilter.ENTITY) { event, _ ->
            event.entity.instance == entity.instance
        }.setPriority(-1)
        eventActions.forEach { (eventType, actions) ->
            node.susListener(eventType.clazz) { event ->
                //Handle Cooldown
                val cooldowns = cooldowns.getOrPut(event.entity.uuid.toString()) { HashMap() }

                if (cooldowns.containsKey(eventType.name)) {
                    if (System.currentTimeMillis() - cooldowns[eventType.name]!! < eventType.cooldown) return@susListener
                }
                cooldowns[eventType.name] = System.currentTimeMillis()

                //Shift click to open edit menu
                if (event is PlayerEntityInteractEvent && event.target == entity && event.player.isSneaking) {
                    NpcEditMenu(this, sandbox).open(event.player)
                }

                //Handle actions
                var player: Player? = null
                var target: Entity = entity
                if (event is PrepareAttackEvent) {
                    if (event.target == entity && event.entity is Player) {
                        player = event.entity as Player
                        target = event.target
                    }
                }

                if (event is PlayerEntityInteractEvent) {
                    if (event.target == entity) {
                        player = event.player
                        target = event.target
                    }
                }

                executeActions(actions, target, player, sandbox, event)
            }
        }

        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    private suspend fun executeActions(actions: MutableList<Action>, entity: Entity, player: Player?, sandbox: Sandbox, event: Event?) {
        ActionExecutor(
            entity,
            player,
            sandbox,
            event,
            actions
        ).execute()
    }
}