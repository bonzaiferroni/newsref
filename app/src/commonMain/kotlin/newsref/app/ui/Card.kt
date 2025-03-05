package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import newsref.app.blip.controls.bg
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors
import newsref.app.blip.theme.ProvideSkyColors
import newsref.app.utils.modifyIfNotNull

@Composable
fun Card(
    shape: Shape = Blip.ruler.rounded,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Column(
            modifier = modifier
                .bg(Blip.localColors.surface, shape)
                .modifyIfNotNull(onClick) { this.clickable(onClick = it) }
                .padding(Blip.ruler.innerPadding)
        ) {
            content()
        }
    }
}