package newsref.app.blip.core

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.nav.*

data class BlipConfig(
    val name: String,
    val logo: ImageVector,
    val home: NavRoute,
    val navGraph: NavGraphBuilder.() -> Unit,
    val portalItems: ImmutableList<PortalItem>,
)
