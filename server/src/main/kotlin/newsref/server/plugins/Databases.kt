package newsref.server.plugins

import io.ktor.server.application.*
import newsref.db.initDb
import newsref.db.readEnvFromDirectory

fun Application.configureDatabases() {
    val env = readEnvFromDirectory("../.env")
    initDb(env)
}


