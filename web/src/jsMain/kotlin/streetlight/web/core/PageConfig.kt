package streetlight.web.core

import io.kvision.html.Div

data class PageConfig(
    val name: String,
    val route: String,
    val navLink: Boolean = false,
    val icon: String = "",
    val builder: PageBuilder
) {
    val linkRoute: String
        get() = "#${route.substringBefore("/:")}"
}

abstract class PageBuilder {

}

data class CachedPageBuilder(
    val content: Div.(AppContext) -> PortalEvents?
) : PageBuilder()

data class IdPageBuilder(
    val content: Div.(AppContext, Int) -> PortalEvents?
) : PageBuilder()

data class TransientPageBuilder(
    val content: Div.(AppContext) -> PortalEvents?
) : PageBuilder()

data class PortalEvents(
    val onLoad: (() -> Unit)? = null,
    val onUnload: (() -> Unit)? = null
)

