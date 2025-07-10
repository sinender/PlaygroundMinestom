package net.sinender.common.postgres

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.InstanceContainer
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.feature.functions.Function
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.postgres.DatabaseFactory.dbQuery
import net.sinender.common.utils.ActionTypeAdapter
import net.sinender.utils.EntityTypeTypeAdapter
import net.sinender.utils.isDouble
import net.sinender.utils.isInt
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.transactions.transaction

private val JsonElement.asAny: Any?
    get() {
        return when {
            isJsonNull -> null
            isJsonPrimitive -> when {
                asString.isInt() -> asInt
                asString.isDouble() -> asDouble
                asJsonPrimitive.isBoolean -> asBoolean
                else -> asString
            }
            isJsonObject -> asJsonObject.asMap().map {
                it.key to it.value.asAny
            }
            isJsonArray -> asJsonArray.map { it.asAny }
            else -> null
        }
    }

class SandboxService(private val db: Database) {
    companion object {
        val gson = GsonBuilder()
            .registerTypeAdapter(Action::class.java, ActionTypeAdapter())
            .registerTypeAdapter(EntityType::class.java, EntityTypeTypeAdapter())
            .create()
    }
    object Sandboxes: Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 36)
        val sandboxUUID = varchar("sandboxUUID", 36).uniqueIndex()
        val ownerUUID = varchar("ownerUUID", 36)
        val events = jsonb("events", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonObject
            val events = mutableMapOf<String, MutableList<Action>>()
            for (entry in json.entrySet()) {
                val key = entry.key
                val value = entry.value.asJsonArray
                val actions = mutableListOf<Action>()
                for (action in value) {
                    val item = ActionEnum.valueOf(action.asJsonObject["enum"].asString)
                    actions.add(gson.fromJson(action, item.clazz))
                }
                events[key] = actions
            }
            events
        })
        val playerVariables = jsonb("playerVariables", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonObject
            val playerVariables = mutableMapOf<String, MutableMap<String, Any?>>()
            for (entry in json.entrySet()) {
                val key = entry.key
                val value = entry.value.asJsonObject
                val vars = mutableMapOf<String, Any?>()
                for (variable in value.entrySet()) {
                    vars[variable.key] = variable.value.asAny
                }
                playerVariables[key] = vars
            }
            playerVariables
        })
        val globalVariables = jsonb("globalVariables", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonObject
            val globalVars = mutableMapOf<String, Any?>()
            for (entry in json.entrySet()) {
                globalVars[entry.key] = entry.value.asAny
            }
            globalVars
        })
        val functions = jsonb("functions", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonArray
            val functions = mutableListOf<Function>()
            for (func in json) {
                val function = gson.fromJson(func, Function::class.java)
                functions.add(function)
            }
            functions
        })
        val npcs = jsonb("npcs", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonArray
            val npcs = mutableListOf<NpcEntity>()
            for (npc in json) {
                val entityType = npc.asJsonObject["entityType"].asString
                npcs.add(gson.fromJson(npc, NpcEntity.getNpcEntity(entityType)?: continue))
            }
            npcs
        })
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(db) {
            SchemaUtils.create(Sandboxes)
        }
    }

    suspend fun createSandbox(name: String, sandboxUUID: String, ownerUUID: String): Sandbox = dbQuery {
        transaction(db) {
            val row = Sandboxes.insert {
                it[Sandboxes.name] = name
                it[Sandboxes.sandboxUUID] = sandboxUUID
                it[Sandboxes.ownerUUID] = ownerUUID
                it[Sandboxes.events] = mutableMapOf()
                it[Sandboxes.playerVariables] = mutableMapOf()
                it[Sandboxes.globalVariables] = mutableMapOf()
                it[Sandboxes.functions] = mutableListOf<Function>()
                it[Sandboxes.npcs] = mutableListOf()
            }
            Sandbox(
                row[Sandboxes.id],
                row[Sandboxes.name],
                row[Sandboxes.sandboxUUID],
                row[Sandboxes.ownerUUID],
                row[Sandboxes.events],
                row[Sandboxes.playerVariables],
                row[Sandboxes.globalVariables],
                row[Sandboxes.functions],
                row[Sandboxes.npcs]
            )
        }
    }

    suspend fun updateSandbox(sandboxUUID: String, sandbox: Sandbox): Boolean = dbQuery {
        transaction(db) {
            Sandboxes.update({ Sandboxes.sandboxUUID eq sandboxUUID }) {
                it[Sandboxes.name] = sandbox.name
                it[Sandboxes.events] = sandbox.events
                it[Sandboxes.playerVariables] = sandbox.playerVariables
                it[Sandboxes.globalVariables] = sandbox.globalVariables
                it[Sandboxes.functions] = sandbox.functions
                it[Sandboxes.npcs] = sandbox.npcs
            } > 0
        }
    }

    suspend fun getAllSandboxes(): List<Sandbox> = dbQuery {
        transaction(db) {
            Sandboxes.selectAll().map {
                Sandbox(
                    it[Sandboxes.id],
                    it[Sandboxes.name],
                    it[Sandboxes.sandboxUUID],
                    it[Sandboxes.ownerUUID],
                    it[Sandboxes.events],
                    it[Sandboxes.playerVariables],
                    it[Sandboxes.globalVariables],
                    it[Sandboxes.functions],
                    it[Sandboxes.npcs]
                )
            }
        }
    }

    suspend fun getSandboxByUUID(uuid: String): Sandbox? = dbQuery {
        transaction(db) {
            Sandboxes.select { Sandboxes.sandboxUUID eq uuid }.map {
                Sandbox(
                    it[Sandboxes.id],
                    it[Sandboxes.name],
                    it[Sandboxes.sandboxUUID],
                    it[Sandboxes.ownerUUID],
                    it[Sandboxes.events],
                    it[Sandboxes.playerVariables],
                    it[Sandboxes.globalVariables],
                    it[Sandboxes.functions],
                    it[Sandboxes.npcs]
                )
            }.firstOrNull()
        }
    }
}

data class Sandbox(
    val id: Int,
    val name: String,
    val sandboxUUID: String,
    val ownerUUID: String,
    var events: MutableMap<String, MutableList<Action>> = mutableMapOf(),
    var playerVariables: MutableMap<String, MutableMap<String, Any?>> = mutableMapOf(),
    var globalVariables: MutableMap<String, Any?> = mutableMapOf(),
    var functions: MutableList<Function> = mutableListOf(),
    var npcs: MutableList<NpcEntity> = mutableListOf(),
    ) {
    var instance: InstanceContainer? = null
}