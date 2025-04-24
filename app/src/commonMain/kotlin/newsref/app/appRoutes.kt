package newsref.app

import pondui.ui.nav.NavRoute
import kotlinx.serialization.Serializable
import newsref.app.ui.FeedSpan

@Serializable
sealed class AppRoute(
    override val title: String,
    val id: Long? = null
) : NavRoute {
    private val titlePath get() = title.lowercase().replace(' ', '-')

    override fun toPath() = id?.let { "$titlePath/$it" } ?: titlePath

    fun matchRoute(path: String) = if (path.startsWith(titlePath)) this else null
}

@Serializable
object StartRoute : AppRoute("Start")

@Serializable
object HelloRoute : AppRoute("Hello")

@Serializable
data class ChapterFeedRoute(val feedSpan: Int = FeedSpan.Week.ordinal) : AppRoute("Stories")

@Serializable
data class ChapterRoute(
    val chapterId: Long,
    val chapterTitle: String?,
    val tab: String? = null,
) : AppRoute(chapterTitle ?: "Chapter: $chapterId")

@Serializable
data class ChapterPageRoute(
    val chapterId: Long,
    val pageId: Long,
    val pageTitle: String?
) : AppRoute(pageTitle ?: "Page: $pageId")

@Serializable
data class PageRoute(
    val pageId: Long,
    val pageTitle: String?
) : AppRoute(pageTitle ?: "Page: $pageId")

@Serializable
object HostFeedRoute : AppRoute("Hosts")

@Serializable
data class HostRoute(val hostId: Int, val core: String) : AppRoute(core)