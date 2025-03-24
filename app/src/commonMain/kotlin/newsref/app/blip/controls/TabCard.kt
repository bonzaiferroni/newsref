package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.ProvideBookColors

@Composable
fun TabCard(
    currentTab: String?,
    onChangePage: (String) -> Unit,
    shape: Shape = RectangleShape,
    modifier: Modifier = Modifier,
    content: @Composable TabScope.() -> Unit
) {
    ProvideBookColors {
        Tabs(
            currentPageName = currentTab,
            onChangePage = onChangePage,
            content = content
        )
    }
}