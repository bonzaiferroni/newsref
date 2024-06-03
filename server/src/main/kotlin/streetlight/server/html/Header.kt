package streetlight.server.html

import kotlinx.html.HTML
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.title

fun HTML.pageHeader(title: String) {
    head {
        title {
            +"$title | streetlight"
        }
        link {
            rel = "stylesheet"
            href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
        }
    }
}