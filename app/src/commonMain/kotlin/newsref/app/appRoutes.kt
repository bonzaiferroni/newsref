package newsref.app

import kotlinx.serialization.Serializable
import newsref.app.blip.nav.NavRoute
import newsref.app.model.SourceBit

@Serializable
sealed class AppRoute(override val title: String) : NavRoute

@Serializable
object StartRoute : AppRoute("Start")

@Serializable
object HelloRoute : AppRoute("Hello")

@Serializable
object ChapterFeedRoute : AppRoute("Chapters")

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