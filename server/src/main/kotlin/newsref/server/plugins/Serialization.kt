package newsref.server.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            json = kotlinx.serialization.json.Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true

            }
        )
    }
}
