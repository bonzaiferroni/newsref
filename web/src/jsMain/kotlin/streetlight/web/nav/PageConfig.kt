package streetlight.web.nav

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

data class BasicPageBuilder(
    val content: Div.() -> Unit
) : PageBuilder()

data class IdPageBuilder(
    val content: Div.(Int) -> Unit
) : PageBuilder()