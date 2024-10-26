package newsref.krawly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.runBlocking
import newsref.db.globalConsole
import newsref.db.models.WebResult
import newsref.krawly.HaltCrawlException
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url
import java.net.NoRouteToHostException

private var console = globalConsole.getHandle("ktor")

private val client = HttpClient(CIO) {
//	engine {
//		followRedirects = true
//		socketTimeout = 30000
//		connectTimeout = 30000
//		connectionRequestTimeout = 30000
//		customizeClient {
//			setMaxConnTotal(1000)
//			setMaxConnPerRoute(100)
//		}
//		customizeRequest {
//			// TODO: request transformations
//		}
//	}
	defaultRequest {
		headers {
			set(HttpHeaders.UserAgent, chromeLinuxAgent)
			extraHeaders.forEach { (key, value) ->
				set(key, value)
			}
			set(HttpHeaders.ContentType, "text/html")
			set(HttpHeaders.AcceptEncoding, "gzip, deflate")
		}
	}
	install(ContentEncoding) {
		gzip()
		deflate()
	}
}

fun ktorFetch(url: Url) = runBlocking { ktorFetchAsync(url) }

suspend fun ktorFetchAsync(url: Url): WebResult {
	return try {
		val response: HttpResponse = client.get(url.href)
		val content: String = response.body()
		WebResult(
			status = response.status.value,
			pageHref = response.call.request.url.toString(),
			content = content,
		)
	} catch (e: HttpRequestTimeoutException) {
		console.logTrace("Arrr! The request timed out!")
		WebResult(timeout = true, exception = e.message)
	} catch (e: Exception) {
//		when (e) {
//			// todo: handle is UnresolvedAddressException,
//			is NoRouteToHostException -> throw HaltCrawlException(e.message ?: "No internet access, halt crawl\n$url")
//		}
		console.logTrace("arr! unknown excpetion!\n$e")
		WebResult(exception = e.message)
	}
}