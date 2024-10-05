package newsref.krawly.utils

import com.microsoft.playwright.*
import it.skrape.selects.Doc
import newsref.krawly.chromeLinuxAgent
import newsref.krawly.console
import newsref.model.core.Url

fun pwFetch(url: Url, screenshot: Boolean = false): WebResult? = Playwright.create().use { playwright ->
    playwright.chromium().launch().use { browser ->
        val page = browser.newContext(contextOptions).newPage()
        page.setViewportSize(1000, 728)
        page.setExtraHTTPHeaders(extraHeaders)
        try {
            val response = page.navigate(url.toString())
            val bytes = if (screenshot) page.screenshot(screenshotOptions) else null
            WebResult(
                status = response.status(),
                doc = page.content().contentToDoc(),
                screenshot = bytes
            )
        } catch (e: TimeoutError) {
            console.logError("pwFetch", "Timeout: $url")
            return null
        } catch (e: PlaywrightException) {
            // adventure mode throws these
            console.logError("pwFetch", "Unusual exception\n${e.message}")
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
)
val screenshotOptions = Page.ScreenshotOptions().apply {
    fullPage = true
}

data class WebResult(
    val status: Int,
    val doc: Doc?,
    val screenshot: ByteArray?,
) {
    fun isSuccess() = status in 200..299
}