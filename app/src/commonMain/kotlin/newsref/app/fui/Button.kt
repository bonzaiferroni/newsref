package newsref.app.fui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Button(
    onClick: () -> Unit,
    background: Color = Fui.colors.primary,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(background)
            .padding(Fui.layout.halfPadding)
    ) {
        content()
    }
}
