package streetlight.web.content

import io.kvision.core.Container
import io.kvision.html.p
import streetlight.web.io.userContext

fun Container.userPage() {
    console.log("User page")
    userContext { userInfo ->
        p("Hello, ${userInfo.username}!")
    }
}