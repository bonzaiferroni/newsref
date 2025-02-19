package newsref.krawly

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

object TestDatabase {

    private val container = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        start()
    }

    fun connect() {
        Database.Companion.connect(
            url = container.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = container.username,
            password = container.password
        )
    }

    fun initDatabase(vararg tables: Table) {
        transaction {
            SchemaUtils.create(*tables)
        }
    }

    fun cleanupDatabase(vararg tables: Table) {
        transaction {
            SchemaUtils.drop(*tables)
        }
    }
}