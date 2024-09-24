package newsref.server.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import newsref.server.db.services.UserService
import newsref.server.db.tables.*
import newsref.server.utilities.DbBackup

fun Application.configureDatabases() {
    val psqlPass = System.getenv("NEWSREF_PSQL_PW")
    val psqldb = Database.connect(
        "jdbc:pgsql://localhost:5432/newsrefdb",
        driver = "com.impossibl.postgres.jdbc.PGDriver",
        user = "newsref",
        password = psqlPass
    )

    transaction(psqldb) {
        SchemaUtils.create(UserTable)
        SchemaUtils.create(SessionTokenTable)
    }

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
