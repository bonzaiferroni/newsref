package newsref.dashboard

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

@Serializable
sealed class ScreenRoute(val title: String = "Title") {
}

@Serializable
data class FeedItemRoute(val feedId: Int) : ScreenRoute("Feed Item")

@Serializable
object FeedTableRoute : ScreenRoute("Feed Table")

@Serializable
data class HelloRoute(val name: String) : ScreenRoute("Hello")

@Serializable
data class SourceItemRoute(
    val sourceId: Long,
    val pageName: String = "",
) : ScreenRoute("Source Item")

@Serializable
object SourceTableRoute : ScreenRoute("Sources")

@Serializable
object StartRoute : ScreenRoute("Start")