package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import newsref.app.blip.theme.*

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Column (
            modifier = modifier.clip(Blip.ruler.rounded)
                .background(Blip.localColors.surface)
                .padding(Blip.ruler.basePadding)
        ) {
            content()
        }
    }
}