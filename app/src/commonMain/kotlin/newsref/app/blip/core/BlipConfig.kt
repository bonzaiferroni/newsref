package newsref.app.blip.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.nav.NavRoute

data class BlipConfig(
    val navGraph: NavGraphBuilder.() -> Unit
)
