package streetlight.web.core

import streetlight.web.pages.aboutPage
import streetlight.web.pages.homePage

val dataPages = listOf(
    PageConfig("Home", "/", "fas fa-home", false, CachedPageBuilder {
        homePage()
    }),
    PageConfig("About", "/about", "fas fa-info", true, CachedPageBuilder {
        aboutPage()
    }),
)