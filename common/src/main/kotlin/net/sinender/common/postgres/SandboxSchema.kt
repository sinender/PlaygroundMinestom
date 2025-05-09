package net.sinender.common.postgres

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.registry.Registry.Entry
import net.sinender.common.action.Action
import net.sinender.common.action.ActionEnum
import net.sinender.common.feature.npc.NpcEntity
import net.sinender.common.postgres.DatabaseFactory.dbQuery
import net.sinender.common.utils.ActionTypeAdapter
import net.sinender.utils.EntityTypeTypeAdapter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.transactions.transaction

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
        val playerStats = jsonb("playerStats", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonObject
            val playerStats = mutableMapOf<String, MutableMap<String, String>>()
            for (entry in json.entrySet()) {
                val key = entry.key
                val value = entry.value.asJsonObject
                val stats = mutableMapOf<String, String>()
                for (stat in value.entrySet()) {
                    stats[stat.key] = stat.value.asString
                }
                playerStats[key] = stats
            }
            playerStats
        })
        val globalStats = jsonb("globalStats", serialize = {
            gson.toJson(it)
        }, deserialize = {
            val json = JsonParser.parseString(it).asJsonObject
            val globalStats = mutableMapOf<String, String>()
            for (entry in json.entrySet()) {
                globalStats[entry.key] = entry.value.asString
            }
            globalStats
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
                it[Sandboxes.events] = mutableMapOf<String, MutableList<Action>>()
                it[Sandboxes.playerStats] = mutableMapOf<String, MutableMap<String, String>>()
                it[Sandboxes.globalStats] = mutableMapOf<String, String>()
                it[Sandboxes.npcs] = mutableListOf<NpcEntity>()
            }
            Sandbox(
                row[Sandboxes.id],
                row[Sandboxes.name],
                row[Sandboxes.sandboxUUID],
                row[Sandboxes.ownerUUID],
                row[Sandboxes.events],
                row[Sandboxes.playerStats],
                row[Sandboxes.globalStats],
                row[Sandboxes.npcs]
            )
        }
    }

    suspend fun updateSandbox(sandboxUUID: String, sandbox: Sandbox): Boolean = dbQuery {
        transaction(db) {
            Sandboxes.update({ Sandboxes.sandboxUUID eq sandboxUUID }) {
                it[Sandboxes.name] = sandbox.name
                it[Sandboxes.events] = sandbox.events
                it[Sandboxes.playerStats] = sandbox.playerStats
                it[Sandboxes.globalStats] = sandbox.globalStats
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
                    it[Sandboxes.playerStats],
                    it[Sandboxes.globalStats],
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
                    it[Sandboxes.playerStats],
                    it[Sandboxes.globalStats],
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
    var playerStats: MutableMap<String, MutableMap<String, String>> = mutableMapOf(),
    var globalStats: MutableMap<String, String> = mutableMapOf(),
    var npcs: MutableList<NpcEntity> = mutableListOf(),
) {
    var instance: InstanceContainer? = null
}