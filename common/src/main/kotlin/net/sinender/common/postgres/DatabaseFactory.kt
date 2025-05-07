package net.sinender.common.postgres

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
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
            println("Windows 11 detected, skipping flyway migration")
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

    fun <T : kotlin.Any> Table.jsonArray(name: String): Column<ArrayList<T>> {
        return jsonb<ArrayList<T>>(name, serialize = { it ->
            val jsonArray = JSONArray()
            it.forEach {
                jsonArray.put(it)
            }
            jsonArray.toString()
        }, deserialize = {
            val users = ArrayList<T>()
            val jsonArray = JSONArray(it)
            for (i in 0 until jsonArray.length()) {
                users.add(jsonArray.get(i) as T)
            }
            users
        }).default(arrayListOf())
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}