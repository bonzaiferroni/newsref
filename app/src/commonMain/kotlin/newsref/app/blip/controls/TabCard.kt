package newsref.app.blip.controls

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors

@Composable
fun TabCard(
    initialTab: String? = null,
    modifyRoute: ((String) -> NavRoute)? = null,
    shape: Shape = RectangleShape, // todo: implement shape
    modifier: Modifier = Modifier, // todo: implement modifier
    content: @Composable TabScope.() -> Unit
) {
    ProvideBookColors {
        Tabs(
            initialTab = initialTab,
            modifyRoute = modifyRoute,
            content = content,
            modifier = modifier
                .shadow(Blip.ruler.shadowElevation, shape)
                .background(Blip.colors.surfaceBook)
                .animateContentSize()
        )
    }
}