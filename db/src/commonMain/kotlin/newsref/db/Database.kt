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
		// todo: add migration handling
		SchemaUtils.create(*dbTables.toTypedArray())
	}
}

val dbTables = listOf(
	// UserTable
	// SessionTokenTable
	SourceTable,
	LinkTable,
	HostTable,
	ArticleTable,
	LeadTable,
	FeedJobTable,
	LeadResultTable,
	SourceContentTable,
	ContentTable,
	HostAuthorTable,
	AuthorTable,
	ScoopTable,
	FeedTable,
)