package newsref.krawly.utils

import com.microsoft.playwright.*
import com.microsoft.playwright.options.RequestOptions
import io.ktor.client.utils.EmptyContent.headers
import it.skrape.selects.Doc
import newsref.db.globalConsole
import newsref.db.utils.fileLog
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url

private val console = globalConsole.getHandle("Playwright")

data class WebResult(
    val pageHref: String? = null,
    val status: Int? = null,
    val doc: Doc? = null,
    val screenshot: ByteArray? = null,
    val requestHeaders: Map<String, String>? = null,
    val timeout: Boolean = false,
    val redirectUrl: String? = null
) {
    fun isSuccess() = status in 200..299
}

data class HeadResult(
    val pageHref: String? = null,
    val redirectHref: String? = null,
    val status: Int? = null,
    val timeout: Boolean = false
) {
    fun isRedirect() = status == 301
}

fun pwFetch(url: Url, screenshot: Boolean = false): WebResult = useChromium { page ->
    var headers: Map<String, String>? = null
    page.onRequest { request ->
        headers = request.headers()
    }
    tryNavigate({
        val response = page.navigate(url.toString())
        val bytes = if (screenshot) page.screenshot(screenshotOptions) else null
        WebResult(
            status = response.status(),
            doc = page.content().contentToDoc(),
            screenshot = bytes,
            pageHref = page.url(),
            requestHeaders = headers
        )
    }, {
        console.logError("Timeout: $url")
        WebResult(timeout = true)
    }, {
        val message = it.message ?: "Unknown error"
        message.fileLog("exceptions", "playwright")
        // adventure mode throws these
        console.logError("Unusual exception: $url\n$message")
        WebResult()
    })
}

fun pwFetchHead(url: Url): HeadResult = useChromium { page ->
    tryNavigate({
        val options = RequestOptions.create().apply {
            setMethod("Head")
        }
        val response = page.request().fetch(url.toString(), options)
        HeadResult(
            status = response.status(),
            pageHref = page.url(),
            redirectHref = response.headers()["location"]
        )
    }, { HeadResult(timeout = true) }, {HeadResult()})
}

private fun <T> useChromium(block: (Page) -> T): T {
    Playwright.create().use { playwright ->
        playwright.chromium().launch().use { browser ->
            val page = browser.newContext(contextOptions).newPage()
            page.setViewportSize(1000, 728)
            page.setExtraHTTPHeaders(extraHeaders)
            return block(page)
        }
    }
}

private fun <T> tryNavigate(
    block: () -> T,
    handleTimeout: (() -> T)? = null,
    handleException: ((PlaywrightException) -> T)? = null
): T {
    return try {
        block()
    } catch (e: TimeoutError) {
        if (handleTimeout != null) {
            handleTimeout()
        } else {
            throw e
        }
    } catch (e: PlaywrightException) {
        if (handleException != null) {
            handleException(e)
        } else {
            throw e
        }
    }
}

private val contextOptions = Browser.NewContextOptions().apply { userAgent = chromeLinuxAgent }
private val launchOptions = BrowserType.LaunchOptions().apply { setHeadless(false) }
private val extraHeaders = mutableMapOf(
    "priority" to "u=0, i",
    "dnt" to "1",
    "accept-language" to "en-US,en;q=0.9",
    "accept-encoding" to "gzip, deflate, br, zstd",
    "sec-ch-ua" to "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"",
    "sec-ch-ua-platform" to "\"Linux\"",
    "sec-ch-ua-mobile" to "?0",
    "sec-fetch-dest" to "document",
    "sec-fetch-mode" to "navigate",
    "sec-fetch-user" to "?1",
    "cache-control" to "max-age=0",
    "upgrade-insecure-requests" to "1"
)
private val screenshotOptions = Page.ScreenshotOptions().apply { fullPage = true }