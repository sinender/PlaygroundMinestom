package net.sinender.common.postgres

import com.google.gson.Gson
import net.sinender.common.postgres.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class SandboxInstanceService(private val db: Database) {
    object SandboxInstances: Table() {
        val id = integer("id").autoIncrement()
        val sandboxUUID = varchar("sandboxUUID", 36).uniqueIndex()
        val instanceArray = binary("instanceArray")
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(db) {
            SchemaUtils.create(SandboxInstances)
        }
    }

    suspend fun createSandbox(sandboxUUID: String, instanceArray: ByteArray): SandboxInstance = dbQuery {
        transaction(db) {
            val row = SandboxInstances.insert {
                it[SandboxInstances.sandboxUUID] = sandboxUUID
                it[SandboxInstances.instanceArray] = instanceArray
            }
            SandboxInstance(
                row[SandboxInstances.sandboxUUID],
                row[SandboxInstances.instanceArray]
            )
        }
    }

    suspend fun getAllSandboxes(): List<SandboxInstance> = dbQuery {
        transaction(db) {
            SandboxInstances.selectAll().map {
                SandboxInstance(
                    it[SandboxInstances.sandboxUUID],
                    it[SandboxInstances.instanceArray],
                )
            }
        }
    }

    suspend fun updateSandbox(sandboxUUID: String, instanceArray: ByteArray): Boolean = dbQuery {
        transaction(db) {
            SandboxInstances.update({ SandboxInstances.sandboxUUID eq sandboxUUID }) {
                it[SandboxInstances.instanceArray] = instanceArray
            } > 0
        }
    }

    suspend fun getSandboxByUUID(uuid: String): SandboxInstance? = dbQuery {
        transaction(db) {
            SandboxInstances.select { SandboxInstances.sandboxUUID eq uuid }.map {
                SandboxInstance(
                    it[SandboxInstances.sandboxUUID],
                    it[SandboxInstances.instanceArray],
                )
            }.firstOrNull()
        }
    }
}

data class SandboxInstance(
    val sandboxUUID: String,
    val instanceArray: ByteArray,
) {
}