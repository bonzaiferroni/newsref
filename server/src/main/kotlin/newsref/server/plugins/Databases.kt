package newsref.server.plugins

import io.ktor.server.application.*
import newsref.db.initDb
import klutch.environment.readEnvFromPath

fun Application.configureDatabases() {
    val env = readEnvFromPath()
    initDb(env)
}


