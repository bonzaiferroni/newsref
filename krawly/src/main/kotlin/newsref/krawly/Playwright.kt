package newsref.krawly

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import java.io.File

fun pwFetch(url: String) = Playwright.create().use { playwright ->
    playwright.chromium().launch().use { browser ->
        val page = browser.newContext(contextOptions).newPage()
        page.setViewportSize(1700, 728)
        page.setExtraHTTPHeaders(extraHeaders)
        page.navigate(url)
        val screenshot = page.screenshot(screenshotOptions)
        val file = File("dump/lastpage.png")
        file.writeBytes(screenshot)
        page.content() // return
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