package newsref.dashboard

import kotlinx.serialization.Serializable

@Serializable
object StartRoute : ScreenRoute("Start")

@Serializable
data class HelloRoute(val name: String) : ScreenRoute("Hello")

@Serializable
open class ScreenRoute(val title: String = "Title") {
}