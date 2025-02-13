package newsref.app.fui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val theme = LocalTheme.current
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(theme.halfPadding)
    ) {
        content()
    }
}
