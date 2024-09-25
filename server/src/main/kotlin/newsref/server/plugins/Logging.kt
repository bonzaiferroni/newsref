package newsref.server.plugins

import io.ktor.server.application.*
import io.ktor.util.logging.*

fun Application.configureLogging() {
    Log.initialize(environment.log)
}