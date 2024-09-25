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

    environment.monitor.subscribe(ApplicationStarted) {
        launch {
            val userCount = UserService().readAll().size
            if (userCount == 0) {
                println("empty db found, restoring from backup")
                DbBackup.restore()
                println("backup restored")
            } else {
                println("creating backup")
                DbBackup.create()
                println("backup created")
            }
        }
    }
}
