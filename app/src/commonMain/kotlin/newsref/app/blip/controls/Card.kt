package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Box (
            modifier = modifier.clip(Blip.ruler.rounded)
                .background(Blip.localColors.surface)
                .padding(Blip.ruler.basePadding)
        ) {
            content()
        }
    }
}