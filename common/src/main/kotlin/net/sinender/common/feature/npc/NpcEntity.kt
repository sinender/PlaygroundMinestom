package net.sinender.common.feature.npc

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.ai.TargetSelector
import net.minestom.server.entity.metadata.golem.IronGolemMeta
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.trait.EntityEvent
import net.minestom.server.timer.TaskSchedule
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.ActionProperty
import net.sinender.common.feature.npc.events.NpcEventType
import net.sinender.common.managers.Sandbox
import org.bson.Document
import kotlin.random.Random

abstract class NpcEntity(
    var id: Int = -1,
    var position: Pos = Pos(0.0, 0.0, 0.0),
    var entityType: EntityType = EntityType.PLAYER,
) {
    var name: String = listOf(
        "§aAlex",
        "§2Baldrick",
        "§cD§6i§ed§ad§by",
        "§5Ben Dover",
        "§7Loading...",
        "§eUpdog",
        "§bCookie Monster",
        "§c❤"
    )[Random.nextInt(9)]
    var lookAtPlayer: Boolean = true
    var eventActions = mutableMapOf<NpcEventType, MutableList<Action>>()
    val properties = mutableListOf<ActionProperty<*>>() //Meta data is stored using properties

    init {
        eventActions = NpcEventType.entries.associateWith {
            mutableListOf<Action>()
        }.toMutableMap()
    }

    abstract fun onSpawn(entityCreature: EntityCreature, sandbox: Sandbox)
    open fun startAI(entityCreature: EntityCreature): Pair<List<GoalSelector>, List<TargetSelector>> {
        // Default AI behavior
        return Pair(listOf(), listOf())
    }

    open fun spawn(sandbox: Sandbox) {
        var entity = EntityCreature(entityType);

        entity.setInstance(sandbox.instance!!, position);
        initListenerAndScheduler(entity, sandbox)
    }

    protected fun initListenerAndScheduler(entity: EntityCreature, sandbox: Sandbox) {
        MinecraftServer.getSchedulerManager().submitTask {
            if (lookAtPlayer) {
                sandbox.instance!!.players.minByOrNull {
                    it.position.distance(entity.position)
                } ?.let { player ->
                    if (player.position.distance(entity.position) > 5) return@let
                    entity.lookAt(player.position.add(0.0, player.eyeHeight, 0.0))
                }
            }
            TaskSchedule.tick(1);
        }

        var node = EventNode.type("npc-event", EventFilter.ENTITY) { event, _ ->
            event.entity.instance == entity.instance
        }
        eventActions.forEach { (eventType, actions) ->
            node.addListener(eventType.clazz) { event ->
                if (event is PlayerEntityInteractEvent && event.target == entity && event.player.isSneaking) {
                    TODO("Open npc menu and make sure there is a cooldown for the stuff below")
                }
                actions.forEach { action ->
                    if (event is PlayerEntityInteractEvent) {
                        if (event.target == entity) {
                            action.execute(event.target, event.player, sandbox, event)
                        }
                    } else {
                        if (event.entity == entity) {
                            action.execute(event.entity, null, sandbox, event)
                        }
                    }
                }
            }
        }

        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    open fun toDocument(): Document {
        val document = Document()
        document["id"] = id
        document["position"] = Document("x", position.x).append("y", position.y).append("z", position.z)
        document["name"] = name
        document["lookAtPlayer"] = lookAtPlayer

        val propertiesDoc = Document()
        properties.forEach {
            propertiesDoc[it.id] = it.toValue()
        }
        document["properties"] = propertiesDoc
        return document
    }

    open fun fromDocument(document: Document) {
        id = document.getInteger("id")
        val posDoc = document.get("position", Document::class.java)
        position = Pos(posDoc.getDouble("x"), posDoc.getDouble("y"), posDoc.getDouble("z"))
        name = document.getString("name")
        lookAtPlayer = document.getBoolean("lookAtPlayer")

        // Load actions


        // Load properties
        val propertiesDoc = document.get("properties", Document::class.java)
        properties.forEach { prop ->
            prop.fromValue(propertiesDoc[prop.id])
            if (prop.value == null) return@forEach
        }
    }
}