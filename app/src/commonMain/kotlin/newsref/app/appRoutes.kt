package newsref.app

import kotlinx.serialization.Serializable
import newsref.app.blip.nav.NavRoute

@Serializable
sealed class AppRoute(override val title: String) : NavRoute

@Serializable
object StartRoute : AppRoute("Start")

@Serializable
object HelloRoute : AppRoute("Hello")

@Serializable
object ChapterFeedRoute : AppRoute("Chapters")