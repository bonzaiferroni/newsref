package newsref.app.pond.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import newsref.app.pond.theme.Pond
import newsref.app.pond.theme.ProvideBookColors

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ProvideBookColors {
        Box(modifier = modifier.background(Pond.localColors.surface)) {
            content()
        }
    }
}