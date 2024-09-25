package newsref.db

import newsref.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb() {
    val password = System.getenv("NEWSREF_PSQL_PW")
    val db = Database.connect(
        "jdbc:pgsql://localhost:5432/newsrefdb",
        driver = "com.impossibl.postgres.jdbc.PGDriver",
        user = "newsref",
        password = password
    )
    transaction(db) {
        // SchemaUtils.create(UserTable)
        // SchemaUtils.create(SessionTokenTable)
        SchemaUtils.create(SourceTable)
        SchemaUtils.create(LinkTable)
    }
}