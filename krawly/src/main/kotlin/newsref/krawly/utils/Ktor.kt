package newsref.krawly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.supervisorScope
import newsref.db.globalConsole
import newsref.db.log.toBlue
import newsref.db.log.toPurple
import newsref.db.models.WebResult
import newsref.db.utils.toFileLog
import newsref.krawly.HaltCrawlException
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url
import java.net.NoRouteToHostException
import java.net.UnknownHostException

private var console = globalConsole.getHandle("ktor")

private val client = HttpClient(Apache) {
	 defaultRequest {
		headers {
			set(HttpHeaders.UserAgent, chromeLinuxAgent)
			extraHeaders.forEach { (key, value) ->
				set(key, value)
			}
			set(HttpHeaders.AcceptEncoding, "gzip, deflate")
		}
	}
	install(ContentEncoding) {
		gzip()
		deflate()
	}
	install(HttpSend) {
		maxSendCount = 30
	}
	engine {
		followRedirects = true
		socketTimeout = 30000
		connectTimeout = 30000
		connectionRequestTimeout = 30000
		customizeClient {
			setMaxConnTotal(1000)
			setMaxConnPerRoute(100)
		}
		customizeRequest {
			// TODO: request transformations
		}
	}
}

suspend fun ktorFetchAsync(url: Url): WebResult = supervisorScope {
	return@supervisorScope try {
		val response: HttpResponse = client.get(url.href)
		val content: String = response.body()
		WebResult(
			status = response.status.value,
			pageHref = response.call.request.url.toString(),
			content = content,
		)
	} catch (e: Exception) {
		when (e) {
			is HttpRequestTimeoutException -> {
				console.logTrace("Arrr! The request timed out!")
				WebResult(timeout = true)
			}
//			is UnknownHostException -> {
//				WebResult(exception = e.message)
//			}
			is NoRouteToHostException -> {
				throw HaltCrawlException(e.message ?: "No internet access, halt crawl\n$url")
			}
			else -> {
				console.logError("Arr! Unknown exception!\n${url.href.toBlue()}\n${e::class.simpleName?.toPurple()}\n${e.message}")
				"$url\n${e.message}".toFileLog("exceptions", "ktor")
				WebResult(exception = e.message)
			}
		}

	}
}
//		when (e) {
//			// todo: handle is UnresolvedAddressException,
//			is NoRouteToHostException -> throw HaltCrawlException(e.message ?: "No internet access, halt crawl\n$url")
//		}
