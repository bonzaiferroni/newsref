package newsref.db

import kotlinx.coroutines.runBlocking
import newsref.db.core.PgVectorManager
import newsref.db.services.UserService
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb() {
	globalConsole.logInfo("initDb", "initializing db")
	val db = connectDb()
	TransactionManager.registerManager(db, PgVectorManager(TransactionManager.manager))
	transaction(db) {
		exec("CREATE EXTENSION IF NOT EXISTS vector;")
		// todo: add migration handling
		SchemaUtils.create(*dbTables.toTypedArray())
		// exec("CREATE INDEX IF NOT EXISTS idx_text_prefix ON content (SUBSTRING(text FROM 1 FOR 100))")
	}
	runBlocking {
		UserService().initUsers()
	}
}

val dbTables = listOf(
	UserTable,
	SessionTokenTable,
	SourceTable,
	SourceScoreTable,
	LinkTable,
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
	FeedSourceTable,
	NoteTable,
	SourceNoteTable,
	SourceVectorTable,
	VectorModelTable,
	SourceDistanceTable,
)

const val URL = "jdbc:postgresql://localhost:5432/newsrefdb"
const val USER = "newsref"
val PASSWORD: String = environment["NEWSREF_PSQL_PW"]

fun connectDb() = Database.connect(
	URL,
	driver = "org.postgresql.Driver",
	user = USER,
	password = PASSWORD
)