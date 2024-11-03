package newsref.krawly.utils

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.util.network.*
import kotlinx.coroutines.supervisorScope
import newsref.db.globalConsole
import newsref.db.log.toBlue
import newsref.db.log.toPurple
import newsref.db.models.WebResult
import newsref.db.utils.toFileLog
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url
import org.htmlunit.org.apache.http.client.CircularRedirectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.URISyntaxException
import java.net.UnknownHostException
import java.nio.charset.MalformedInputException
import java.nio.charset.UnmappableCharacterException
import javax.net.ssl.SSLHandshakeException

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
		maxSendCount = 40
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
			// extra-serious exceptions
			is UnresolvedAddressException,
			is NoRouteToHostException -> {
				WebResult(noConnection = true)
			}
			// timeouts
			is ConnectTimeoutException,
			is HttpRequestTimeoutException -> {
				console.logTrace("Arrr! The request timed out!")
				WebResult(timeout = true)
			}
			// un-serious exceptions
			is IllegalStateException,
			is SocketException,
			is SocketTimeoutException,
			is CircularRedirectException,
			is UnmappableCharacterException,
			is MalformedInputException,
			is URISyntaxException,
			is SSLHandshakeException,
			is UnknownHostException -> {
				WebResult(exception = e.message)
			}
			// mystery exceptions
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
//		}
