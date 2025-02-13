package newsref.server.plugins

import io.ktor.server.application.*
import newsref.db.globalConsole
import newsref.db.initDb
import newsref.db.models.User
import newsref.db.readEnvFromDirectory
import newsref.db.tables.UserRow
import newsref.db.tables.UserTable
import newsref.db.tables.fromData
import newsref.db.utils.RESOURCE_PATH
import newsref.db.utils.createOrUpdate
import newsref.db.utils.jsonDecoder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File

fun Application.configureDatabases() {
    val env = readEnvFromDirectory("../.env")
    initDb(env)
}


