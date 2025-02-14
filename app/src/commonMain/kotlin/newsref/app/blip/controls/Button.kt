package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import newsref.app.blip.theme.Blip

@Composable
fun Button(
    onClick: () -> Unit,
    background: Color = Blip.colors.primary,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(background)
            .padding(Blip.ruler.halfPadding)
    ) {
        content()
    }
}
