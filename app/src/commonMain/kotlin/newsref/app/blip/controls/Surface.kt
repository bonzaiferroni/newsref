package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideBookColors

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ProvideBookColors {
        Box(modifier = modifier.background(Blip.localColors.surface)) {
            content()
        }
    }
}