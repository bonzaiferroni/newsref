package newsref.db

import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun generateMigrationScript() {
	migrate("test", false)
	migrate("apply", true)
}

@OptIn(ExperimentalDatabaseMigrationApi::class)
private fun migrate(protocol: String, applyMigration: Boolean) {
	val folder = File("$MIGRATIONS_DIRECTORY/$protocol")
	if (!folder.exists()) folder.mkdirs()
	val file = folder.listFiles()?.firstNotNullOfOrNull {
		if (it.name.endsWith(".sql")) null
		else if (File("${it.absolutePath}.sql").exists()) null
		else it
	} ?: return

	val name = file.name
	file.delete()

	val isBaseline = folder.listFiles()?.count { it.isFile && it.name.endsWith(".sql") } == 0
	connectDb()

	transaction {
		MigrationUtils.generateMigrationScript(
			*dbTables.toTypedArray(),
			scriptDirectory = folder.absolutePath,
			scriptName = name,
		)
	}

	if (!applyMigration) return

	val flyway = Flyway.configure()
		.dataSource(URL, USER, PASSWORD)
		.locations("filesystem:$MIGRATIONS_DIRECTORY")
		.baselineOnMigrate(isBaseline) // Used when migrating an existing database for the first time
		.load()
	flyway.migrate()
}

const val MIGRATIONS_DIRECTORY = "migrations" // Location of migration scripts