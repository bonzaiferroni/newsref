package newsref.krawly

import com.microsoft.playwright.Browser
import com.microsoft.playwright.Playwright
import okhttp3.internal.userAgent

val options = Browser.NewContextOptions().apply { userAgent = chromeAgent }

fun pwFetch(url: String) = Playwright.create().use { playwright ->
    playwright.chromium().launch().use { browser ->
        val page = browser.newContext(options).newPage()
        page.navigate(url)
        page.content() // return
    }
}