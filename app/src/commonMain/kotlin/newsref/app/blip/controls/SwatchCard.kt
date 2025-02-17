package newsref.app.blip.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import newsref.app.blip.theme.*

@Composable
fun SwatchCard(
    swatchIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        val shape = RoundedCornerShape(
            topStartPercent = 0,
            topEndPercent = 50,
            bottomStartPercent = 0,
            bottomEndPercent = 50
        )
        val swatches = Blip.colors.swatches
        val swatchColor = swatches[swatchIndex % swatches.size]
        Box(
            modifier = modifier
                .lightedBg(swatchColor, Blip.colors.accent, 0f, shape)
                .padding(Blip.ruler.baseSpacing, 0.dp, 0.dp, 0.dp)
        ) {
            Column (
                modifier = modifier
                    .lightedBg(Blip.localColors.surface, Blip.colors.accent, 0f, shape)
                    .padding(Blip.ruler.basePadding)
            ) {
                content()
            }
        }
    }
}