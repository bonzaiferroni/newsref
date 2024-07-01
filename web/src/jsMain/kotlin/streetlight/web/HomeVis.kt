package streetlight.web

import io.kvision.core.Container
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.rest.callDynamic
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.utils.px
import kotlinx.serialization.Serializable
import kotlin.js.Promise

@Serializable
data class RequestInfo(
    val id: Int,
    val eventId: Int,
    val locationName: String,
    val performanceId: Int,
    val performanceName: String,
    val time: Long = 0L,
    val performed: Boolean = false,
)

fun Container.homeVis() {
    val count = ObservableValue(0)
    val restClient = RestClient()
    val result: Promise<List<RequestInfo>> = restClient.call("http://localhost:8080/api/v1/request_info")

    div {
        padding = 10.px
        vPanel(spacing = 5) {
            h1().bind(count) {
                +"World count = $it"
            }
            button("+") {
                onClick {
                    count.value++
                }
            }
            div("1")
            div("2")
            div("3")
            val text = text(label = "Enter something")
            div().bind(text) {
                +"You entered: $it"
            }
        }
    }
}