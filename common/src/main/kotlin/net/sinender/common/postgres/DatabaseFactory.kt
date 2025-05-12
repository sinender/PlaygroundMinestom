package net.sinender.common.postgres

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import net.minestom.server.MinecraftServer
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.json.JSONArray


lateinit var db: Database
lateinit var sandboxService: SandboxService
lateinit var sandboxInstanceService: SandboxInstanceService
object DatabaseFactory {

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val maxPoolSize = 50
        val autoCommit = false
        val connectionPool = createHikariDataSource(
            url = "jdbc:postgresql://localhost:5432/playground?user=postgres&password=password",
            driver = driverClassName,
            maxPoolSize.toInt(),
            autoCommit
        )
        val database = Database.connect(connectionPool)
        db = database

        sandboxService = SandboxService(db)
        sandboxInstanceService = SandboxInstanceService(db)

        if (System.getProperty("os.name").equals("Windows 11")) {
            MinecraftServer.LOGGER.warn("Windows 11 detected, skipping flyway migration")
            return
        }
        val flyway = Flyway.configure().dataSource(connectionPool).load()
        flyway.migrate()
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        maxPoolSize: Int,
        autoCommit: Boolean
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = maxPoolSize
        isAutoCommit = autoCommit
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}