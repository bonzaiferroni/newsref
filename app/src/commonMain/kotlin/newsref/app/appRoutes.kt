package newsref.app

import kotlinx.serialization.Serializable
import newsref.app.blip.nav.NavRoute
import newsref.app.model.Chapter
import newsref.app.model.ChapterPack
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
data class ChapterRoute(val id: Long, val chapterTitle: String?) : AppRoute("Chapter: $chapterTitle")

@Serializable
data class ChapterSourceRoute(val sourceBit: SourceBit) : AppRoute("Chapter")