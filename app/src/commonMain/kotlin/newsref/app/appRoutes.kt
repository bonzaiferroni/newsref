package newsref.app

import kotlinx.serialization.Serializable
import newsref.app.blip.nav.NavRoute
import newsref.app.model.SourceBit
import newsref.app.ui.FeedSpan

@Serializable
sealed class AppRoute(
    override val title: String,
) : NavRoute

@Serializable
object StartRoute : AppRoute("Start")

@Serializable
object HelloRoute : AppRoute("Hello")

@Serializable
data class ChapterFeedRoute(val feedSpan: Int = FeedSpan.Week.ordinal) : AppRoute("Chapters")

@Serializable
data class ChapterRoute(
    val id: Long,
    val chapterTitle: String?
) : AppRoute(chapterTitle ?: "Chapter: $id")

@Serializable
data class ChapterSourceRoute(
    val chapterId: Long,
    val pageId: Long,
    val sourceTitle: String?
) : AppRoute(sourceTitle ?: "Source: $pageId")

@Serializable
data class SourceRoute(val pageId: Long) : AppRoute("Source")

@Serializable
object HostFeedRoute : AppRoute("Hosts")

@Serializable
data class HostRoute(val hostId: Int, val core: String) : AppRoute(core)