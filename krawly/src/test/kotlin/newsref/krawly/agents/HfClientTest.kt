package newsref.krawly.agents

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test

val ktor = HttpClient(CIO) {
	install(ContentNegotiation) {
		json(Json {
			prettyPrint = true
			isLenient = true
			ignoreUnknownKeys = true
		})
	}
	defaultRequest {
		headers {
			set(HttpHeaders.ContentType, "application/json")
		}
	}
	engine {
		requestTimeout = 120_000 // Timeout in milliseconds (30 seconds here)
	}
	install(HttpTimeout) {
		requestTimeoutMillis = 120_000 // Set request timeout
		connectTimeoutMillis = 120_000 // Set connection timeout
		socketTimeoutMillis = 120_000  // Set socket timeout
	}
}

class HfClientTest {
	@Test
	fun `explore code`() = runBlocking {
		val response = ktor.post("http://localhost:8000/models/my_model")
		println(response.bodyAsText())
	}
}