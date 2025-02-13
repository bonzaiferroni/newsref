package newsref.dashboard

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Serializable
sealed class ScreenRoute(val title: String = "Title") {
}

@Serializable
data class FeedItemRoute(val feedId: Int) : ScreenRoute("Feed Item")

@Serializable
data class FeedTableRoute(val page: String? = null) : ScreenRoute("Feed Table")

@Serializable
data class HelloRoute(val name: String) : ScreenRoute("Hello")

@Serializable
data class SourceItemRoute(
    val sourceId: Long,
    val pageName: String = "",
    val nextSpeakContent: List<Long>? = null
) : ScreenRoute("Source Item")

@Serializable
object SourceTableRoute : ScreenRoute("Sources")

@Serializable
data class StartRoute(val days: Int = 7) : ScreenRoute("Start")

@Serializable
object ChapterTableRoute : ScreenRoute("Chapters")

@Serializable
data class ChapterItemRoute(val chapterId: Long) : ScreenRoute("Chapter")

@Serializable
data class HostTableRoute(val searchText: String? = null) : ScreenRoute("Hosts")

@Serializable
data class HostItemRoute(val hostId: Int, val page: String = "Sources") : ScreenRoute("Host")

@Serializable
data class ChartBoardRoute(val page: String? = null) : ScreenRoute("Chart Board")

@Serializable
data class StoryTableRoute(val searchText: String? = null) : ScreenRoute("Story Table")

@Serializable
data class StoryItemRoute(val storyId: Long, val page: String? = null) : ScreenRoute("Story Item")