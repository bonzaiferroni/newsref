package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import newsref.app.pond.controls.bg
import newsref.app.pond.theme.Pond
import newsref.app.pond.theme.ProvideBookColors
import newsref.app.utils.modifyIfNotNull

@Composable
fun Card(
    shape: Shape = Pond.ruler.rounded,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Column(
            modifier = modifier
                .bg(Pond.localColors.surface, shape)
                .modifyIfNotNull(onClick) { this.clickable(onClick = it) }
                .padding(Pond.ruler.innerPadding)
        ) {
            content()
        }
    }
}