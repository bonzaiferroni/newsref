package newsref.db

import klutch.db.tables.UserTable
import klutch.environment.Environment
import kotlinx.coroutines.runBlocking
import newsref.db.core.PgVectorManager
import newsref.db.services.UserInitService
import newsref.db.tables.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

fun initDb(env: Environment) {
	globalConsole.logInfo("initDb", "initializing db")
	val db = connectDb(env)
	TransactionManager.registerManager(db, PgVectorManager(TransactionManager.manager))
	transaction(db) {
		exec("CREATE EXTENSION IF NOT EXISTS vector;")
		// todo: add migration handling
		SchemaUtils.create(*dbTables.toTypedArray())
		// exec("CREATE INDEX IF NOT EXISTS idx_text_prefix ON content (SUBSTRING(text FROM 1 FOR 100))")
	}
	runBlocking {
		UserInitService().initUsers()
	}
}

val dbTables = listOf(
    UserTable,
	RefreshTokenTable,
	PageTable,
	PageTable,
	PageScoreTable,
	LinkTable,
	HostTable,
	LeadTable,
	LeadJobTable,
	LeadResultTable,
	PageContentTable,
	ContentTable,
	HostAuthorTable,
	PageAuthorTable,
	AuthorTable,
	ScoopTable,
	FeedTable,
	NexusTable,
	PageCacheTable,
	NoteTable,
	PageNoteTable,
	EmbeddingTable,
	EmbeddingFamilyTable,
	StoryTable,
	ChapterTable,
	ChapterPageTable,
	ChapterExclusionTable,
	FeedPageTable,
	LogVariableTable,
	IntegerLogTable,
	JsonLogTable,
	LocationTable,
	PersonTable,
	PagePersonTable,
	HuddleTable,
	HuddleCommentTable,
	HuddleResponseTable,
	CommentTable,
	LogTable,
	ChapterPersonTable,
)

const val URL = "jdbc:postgresql://localhost:5432/newsrefdb"
const val USER = "newsref"
const val PASSWORD_KEY = "NEWSREF_PSQL_PW"

fun connectDb(env: Environment) = Database.connect(
    url = URL,
	driver = "org.postgresql.Driver",
	user = USER,
	password = env.read(PASSWORD_KEY)
)