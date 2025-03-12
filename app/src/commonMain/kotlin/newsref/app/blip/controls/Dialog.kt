package newsref.app.blip.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import newsref.app.blip.theme.Blip

@Composable
fun DialogOld(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
        ) {
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 200.dp, maxWidth = 400.dp, minHeight = 100.dp)
                    .background(Color.White, shape = Blip.ruler.rounded)
                    .padding(Blip.ruler.halfPadding)
            ) {
                content()
            }
        }
    }
}