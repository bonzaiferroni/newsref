package newsref.app.pond.controls

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import newsref.app.pond.theme.Pond

@Composable
fun Icon(
    imageVector: ImageVector,
    tint: Color = Pond.localColors.content,
    modifier: Modifier = Modifier
) {
    val colorFilter =
        remember(tint) { if (tint == Color.Unspecified) null else ColorFilter.tint(tint) }
    Box(
        modifier
            .toolingGraphicsLayer()
            .paint(
                painter = rememberVectorPainter(imageVector),
                colorFilter = colorFilter,
                contentScale = ContentScale.Fit
            )
    )
}