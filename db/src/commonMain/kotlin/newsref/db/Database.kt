package newsref.db

import newsref.db.tables.*
import newsref.model.core.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb() {
	println("initDb: initializing db")
	val db = connectDb()
	transaction(db) {
		// todo: add migration handling
		SchemaUtils.create(*dbTables.toTypedArray())
		// exec("CREATE INDEX IF NOT EXISTS idx_text_prefix ON content (SUBSTRING(text FROM 1 FOR 100))")
	}
}

val dbTables = listOf(
	UserTable,
	SessionTokenTable,
	SourceTable,
	LinkTable,
	LinkScoreTable,
	HostTable,
	ArticleTable,
	LeadTable,
	LeadJobTable,
	LeadResultTable,
	SourceContentTable,
	ContentTable,
	HostAuthorTable,
	SourceAuthorTable,
	AuthorTable,
	ScoopTable,
	FeedTable,
	NexusTable,
)

const val URL = "jdbc:postgresql://localhost:5432/newsrefdb"
const val USER = "newsref"
val PASSWORD: String = System.getenv("NEWSREF_PSQL_PW")

fun connectDb() = Database.connect(
	URL,
	driver = "org.postgresql.Driver",
	user = USER,
	password = PASSWORD
)