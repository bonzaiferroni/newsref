package newsref.krawly.utils

import com.microsoft.playwright.*
import it.skrape.selects.Doc
import newsref.db.globalConsole
import newsref.krawly.chromeLinuxAgent
import newsref.model.core.Url

private val console = globalConsole.getHandle("Playwright")

fun pwFetch(url: Url, screenshot: Boolean = false): WebResult? = Playwright.create().use { playwright ->
    playwright.chromium().launch().use { browser ->
        if (url.isInvalidSuffix()) return null
        val page = browser.newContext(contextOptions).newPage()
        page.setViewportSize(1000, 728)
        page.setExtraHTTPHeaders(extraHeaders)
        var headers: Map<String, String>? = null
        page.onRequest { request ->
            headers = request.headers()
        }
        try {
            val response = page.navigate(url.toString())
            val bytes = if (screenshot) page.screenshot(screenshotOptions) else null
            WebResult(
                status = response.status(),
                doc = page.content().contentToDoc(),
                screenshot = bytes,
                url = page.url(),
                requestHeaders = headers
            )
        } catch (e: TimeoutError) {
            console.logError("Timeout: $url")
            return null
        } catch (e: PlaywrightException) {
            // adventure mode throws these
            console.logError("Unusual exception\n${e.message}")
            return null
        }
    }
}

val contextOptions = Browser.NewContextOptions().apply { userAgent = chromeLinuxAgent }
val launchOptions = BrowserType.LaunchOptions().apply {
    setHeadless(false)
}
val extraHeaders = mutableMapOf(
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
val screenshotOptions = Page.ScreenshotOptions().apply {
    fullPage = true
}

data class WebResult(
    val status: Int,
    val doc: Doc?,
    val screenshot: ByteArray?,
    val url: String,
    val requestHeaders: Map<String, String>?,
) {
    fun isSuccess() = status in 200..299
}