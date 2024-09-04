package streetlight.web.nav

import io.kvision.core.Container

data class PageConfig(
    val name: String,
    val route: String,
    val icon: String,
    val content: Container.() -> Unit
) {
    val hashRoute: String
        get() = "#$route"
}