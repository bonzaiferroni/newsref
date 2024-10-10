package newsref.krawly.utils

import com.microsoft.playwright.*
import com.microsoft.playwright.options.RequestOptions
import it.skrape.selects.Doc
import newsref.db.globalConsole
import newsref.db.utils.fileLog
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url

private val console = globalConsole.getHandle("Playwright")

data class WebResult(
    val pageUrl: String? = null,
    val status: Int? = null,
    val doc: Doc? = null,
    val screenshot: ByteArray? = null,
    val requestHeaders: Map<String, String>? = null,
    val timeout: Boolean = false
) {
    fun isSuccess() = status in 200..299
}

fun pwFetch(url: Url, screenshot: Boolean = false): WebResult? = useChromium(url) { page ->
    if (url.isInvalidSuffix()) return@useChromium null
    val response = page.navigate(url.toString())
    val bytes = if (screenshot) page.screenshot(screenshotOptions) else null
    WebResult(
        status = response.status(),
        doc = page.content().contentToDoc(),
        screenshot = bytes,
        pageUrl = page.url(),
    )
}

fun pwFetchHead(url: Url): WebResult? = useChromium(url) { page ->
    // Use the fetch API for sending a HEAD request
    val options = RequestOptions.create().apply {
        setMethod("Head")
    }
    val response = page.request().fetch(url.toString(), options)
    WebResult(
        status = response.status(),
        doc = page.content().contentToDoc(),
        pageUrl = page.url(),
    )
}

private fun useChromium(url: Url, block: (Page) -> WebResult?): WebResult? {
    Playwright.create().use { playwright ->
        playwright.chromium().launch().use { browser ->
            val page = browser.newContext(contextOptions).newPage()
            page.setViewportSize(1000, 728)
            page.setExtraHTTPHeaders(extraHeaders)
            var headers: Map<String, String>? = null
            page.onRequest { request ->
                headers = request.headers()
            }
            try {
                val result = block(page)
                return result?.copy(requestHeaders = headers)
            } catch (e: TimeoutError) {
                console.logError("Timeout: $url")
                return WebResult(timeout = true)
            } catch (e: PlaywrightException) {
                val message = e.message ?: "Unknown error"
                message.fileLog("exceptions", "playwright")
                // adventure mode throws these
                console.logError("Unusual exception: $url\n$message")
                return null
            }
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