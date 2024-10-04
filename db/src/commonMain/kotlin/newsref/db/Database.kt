package newsref.db

import newsref.db.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb() {
    println("initDb: initializing db")
    val password = System.getenv("NEWSREF_PSQL_PW")
    val db = Database.connect(
        "jdbc:postgresql://localhost:5432/newsrefdb",
        driver = "org.postgresql.Driver",
        user = "newsref",
        password = password
    )
    transaction(db) {
        // SchemaUtils.create(UserTable)
        // SchemaUtils.create(SessionTokenTable)
        SchemaUtils.create(SourceTable)
        SchemaUtils.create(LinkTable)
        SchemaUtils.create(OutletTable)
        SchemaUtils.create(ArticleTable)
        SchemaUtils.create(LeadTable)
        SchemaUtils.create(LeadJobTable)
        SchemaUtils.create(SourceContentTable)
        SchemaUtils.create(ContentTable)
        SchemaUtils.create(OutletAuthorTable)
        SchemaUtils.create(AuthorTable)
        SchemaUtils.create(ScoopTable)
        SchemaUtils.create(FeedTable)
    }
}