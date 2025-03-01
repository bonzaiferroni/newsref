package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import newsref.app.blip.controls.bg
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        val shape = RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 50,
            bottomStartPercent = 50,
            bottomEndPercent = 50
        )
        Column(
            modifier = modifier
                .bg(Blip.localColors.surface, shape)
                .padding(Blip.ruler.innerPadding)
        ) {
            content()
        }
    }
}