package newsref.dashboard

import kotlinx.serialization.Serializable

@Serializable
sealed class DashRoute(val title: String = "Title") {
}

@Serializable
data class FeedItemRoute(val feedId: Int) : DashRoute("Feed Item")

@Serializable
data class FeedTableRoute(val page: String? = null) : DashRoute("Feed Table")

@Serializable
data class HelloRoute(val name: String) : DashRoute("Hello")

@Serializable
data class SourceItemRoute(
    val sourceId: Long,
    val pageName: String = "",
    val nextSpeakContent: List<Long>? = null
) : DashRoute("Source Item")

@Serializable
object SourceTableRoute : DashRoute("Sources")

@Serializable
data class StartRoute(val days: Int = 7) : DashRoute("Start")

@Serializable
object ChapterTableRoute : DashRoute("Chapters")

@Serializable
data class ChapterItemRoute(val chapterId: Long) : DashRoute("Chapter")

@Serializable
data class HostTableRoute(val searchText: String? = null) : DashRoute("Hosts")

@Serializable
data class HostItemRoute(val hostId: Int, val page: String = "Sources") : DashRoute("Host")

@Serializable
data class ChartBoardRoute(val page: String? = null) : DashRoute("Chart Board")

@Serializable
data class StoryTableRoute(val searchText: String? = null) : DashRoute("Story Table")

@Serializable
data class StoryItemRoute(val storyId: Long, val page: String? = null) : DashRoute("Story Item")