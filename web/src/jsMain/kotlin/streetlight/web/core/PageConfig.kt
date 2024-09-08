package streetlight.web.core

import io.kvision.html.Div

data class PageConfig(
    val name: String,
    val route: String,
    val icon: String,
    val navLink: Boolean,
    val builder: PageBuilder
) {
    val linkRoute: String
        get() = "#${route.substringBefore("/:")}"
}

abstract class PageBuilder {

}

data class CachedPageBuilder(
    val content: Div.() -> PortalEvents?
) : PageBuilder()

data class IdPageBuilder(
    val content: Div.(Int) -> PortalEvents?
) : PageBuilder()

data class TransientPageBuilder(
    val content: Div.() -> PortalEvents?
) : PageBuilder()

data class PortalEvents(
    val onLoad: (() -> Unit)? = null,
    val onUnload: (() -> Unit)? = null
)

