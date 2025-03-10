package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import newsref.app.blip.theme.Blip

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Blip.localColors.surface)
            .then(modifier)
    ) {
        content()
    }
}