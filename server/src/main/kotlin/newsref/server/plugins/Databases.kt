package newsref.server.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.launch
import newsref.db.initDb
import newsref.db.tables.SessionTokenTable
import newsref.db.tables.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import newsref.server.db.services.UserService
import newsref.server.utilities.DbBackup

fun Application.configureDatabases() {
    initDb()
}
