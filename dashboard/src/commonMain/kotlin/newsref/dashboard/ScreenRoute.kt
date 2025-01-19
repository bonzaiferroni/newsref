package newsref.dashboard

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import newsref.dashboard.ui.screens.StartRoute

@Serializable
open class ScreenRoute(val title: String = "Title") {
}