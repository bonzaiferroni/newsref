package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.Blip
import newsref.app.model.*

@Composable
fun SourceArray(sources: ImmutableList<ArticleBit>, color: Color) {
    val ruler = Blip.ruler
    Row(
        horizontalArrangement = ruler.rowTight
    ) {
        for (source in sources) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ruler.round)
                    .background(color)
            ) {
                source.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}