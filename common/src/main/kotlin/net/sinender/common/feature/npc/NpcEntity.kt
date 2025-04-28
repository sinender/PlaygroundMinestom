package net.sinender.common.feature.npc

import io.github.togar2.pvp.events.PrepareAttackEvent
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.ai.TargetSelector
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.entity.EntityAttackEvent
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.item.Material
import net.minestom.server.timer.TaskSchedule
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.action.ActionProperty
import net.sinender.common.action.properties.BooleanProperty
import net.sinender.common.action.properties.boolean
import net.sinender.common.feature.npc.events.NpcEventType
import net.sinender.common.feature.npc.menu.NpcEditMenu
import net.sinender.common.managers.Sandbox
import net.sinender.common.menu.MenuItem
import net.sinender.common.menu.menuItem
import org.bson.Document
import kotlin.random.Random

abstract class NpcEntity(
    var id: Int = -1,
    var position: Pos = Pos(0.0, 0.0, 0.0),
    var entityType: EntityType = EntityType.PLAYER,
) {
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
    val properties = mutableListOf<ActionProperty<*>>() //Meta data is stored using properties

    init {
        properties.add(
            BooleanProperty(
                "lookAtPlayer",
                "Look at player",
                "Whether the NPC should look at the player.",
                true,
            )
        )
        eventActions = NpcEventType.entries.associateWith {
            mutableListOf<Action>()
        }.toMutableMap()
    }

    open fun distinctMenuItem(): MenuItem {
        var item = menuItem(Material.SKELETON_SKULL)
            .name("<yellow>$name")

        item.info("<yellow>Properties")
        for (prop in properties) {
            item = item.info(prop.displayName, prop.displayValue()?: continue)
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
        initListenerAndScheduler(entity, sandbox)
    }

    val cooldowns = mutableMapOf<String, Long>()
    protected fun initListenerAndScheduler(entity: EntityCreature, sandbox: Sandbox) {
        MinecraftServer.getSchedulerManager().submitTask {
            if (properties.boolean("lookAtPlayer") == true) {
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
        }.setPriority(-1)
        eventActions.forEach { (eventType, actions) ->
            node.addListener(eventType.clazz) { event ->
                //Handle Cooldown
                if (cooldowns.containsKey(eventType.name)) {
                    if (System.currentTimeMillis() - cooldowns[eventType.name]!! < eventType.cooldown) return@addListener
                }
                cooldowns[eventType.name] = System.currentTimeMillis()

                //Shift click to open edit menu
                if (event is PlayerEntityInteractEvent && event.target == entity && event.player.isSneaking) {
                    NpcEditMenu(this, sandbox).open(event.player)
                }

                //Handle actions
                actions.forEach { action ->
                    //Check special cases
                    if (event is PrepareAttackEvent){
                        if (event.target == entity && event.entity is Player) {
                            action.execute(event.target, event.entity as Player, sandbox, event)
                            return@forEach
                        }
                    }

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
        // Save actions
        val actionsDoc = Document()
        eventActions.forEach { (eventType, actions) ->
            val actionsList = ArrayList<Document>()
            actions.forEach { action ->
                actionsList.add(action.toDocument())
            }
            actionsDoc[eventType.name] = actionsList
        }
        document["actions"] = actionsDoc
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

        // Load actions
        val actionsDoc = document.get("actions", Document::class.java)
        eventActions.forEach { (eventType, actions) ->
            val actionsList = actionsDoc.get(eventType.name, ArrayList::class.java) as List<Document>
            actionsList.forEach { actionDoc ->
                val action = ActionEnum.fromId(actionDoc.getString("id"))?.fromDocument(actionDoc)
                if (action != null) {
                    actions.add(action)
                }
            }
        }

        // Load properties
        val propertiesDoc = document.get("properties", Document::class.java)
        properties.forEach { prop ->
            prop.fromValue(propertiesDoc[prop.id])
            if (prop.value == null) return@forEach
        }
    }
}