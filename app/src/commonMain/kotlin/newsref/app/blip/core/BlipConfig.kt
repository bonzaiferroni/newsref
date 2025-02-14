package newsref.app.blip.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.nav.*

data class BlipConfig(
    val logo: ImageVector,
    val home: NavRoute,
    val navGraph: NavGraphBuilder.() -> Unit,
    val portalActions: ImmutableList<PortalAction>
)
