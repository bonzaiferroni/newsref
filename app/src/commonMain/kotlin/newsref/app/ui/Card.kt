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

@Composable
fun Card(
    shape: Shape = RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomStartPercent = 50,
        bottomEndPercent = 50
    ),
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Column(
            modifier = modifier
                .bg(Blip.localColors.surface, shape)
                .clickable(onClick = onClick)
                .padding(Blip.ruler.innerPadding)
        ) {
            content()
        }
    }
}