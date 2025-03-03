package newsref.app.blip.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors
import newsref.app.ui.Card

@Composable
fun TabCard(
    currentPageName: String?,
    onChangePage: (String) -> Unit,
    pages: ImmutableList<TabPage>,
    shape: Shape = RectangleShape,
    modifier: Modifier = Modifier
) {
    ProvideBookColors {
        TabPages(
            currentPageName,
            onChangePage,
            pages,
        )
    }
}