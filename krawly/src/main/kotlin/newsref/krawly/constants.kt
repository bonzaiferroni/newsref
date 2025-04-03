package newsref.krawly

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlin.text.StringBuilder

val firefoxAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0"
val firefoxLinuxAgent = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:130.0) Gecko/20100101 Firefox/130.0"
val chromeAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
val chromeLinuxAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36"
val safariAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.6 Safari/605.1.15"

//private val lead = "https://www.politico.com/news/2024/09/26/newsom-signs-reparations-apology-00181368"
//private val lead = "https://www.theatlantic.com/newsletters/archive/2024/09/books-briefing-millennials-gen-x-youth-intermezzo/680048/"
//private val lead = "https://www.nytimes.com/2024/08/28/us/politics/trump-arlington-cemetery.html"
//private val lead = "https://www.reuters.com/world/middle-east/israel-continue-ceasefire-discussions-lebanon-netanyahu-says-2024-09-27/"
//private val lead = "https://www.axios.com/2024/09/27/hurricane-helene-floods-florida-georgia-north-carolina"
//private val lead = "https://www.npr.org/2024/09/28/nx-s1-5132035/death-toll-from-hurricane-helene-mounts-as-aftermath-assessment-begins"
//private val lead = "https://apnews.com/article/trump-wisconsin-immigration-visit-arrest-0810efccefc8b094756d0c46f927b7dc"
//private val lead = "https://www.timesofisrael.com/liveblog_entry/senior-iaf-officials-say-strike-that-killed-nasrallah-pulled-off-flawlessly/"
//private val lead = "https://www.wsj.com/world/middle-east/israel-brings-fight-to-beirut-still-assessing-whether-hezbollahsleader-is-dead-1bf0d098"

const val MAX_URL_ATTEMPTS = 5
const val MAX_URL_CHARS = 400

val globalKtor = HttpClient(CIO) {
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
//    install(Logging) {
//        logger = Logger.SIMPLE
//        level = LogLevel.BODY
//        filter { request ->
//            request.url.encodedPath.contains("generateContent")
//        }
//        sanitizeHeader { header -> header == HttpHeaders.Authorization }
//    }
}