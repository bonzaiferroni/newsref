package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import newsref.app.blip.theme.Blip
import newsref.app.model.Source

@Composable
fun SourceArray(sources: ImmutableList<Source>) {
    val ruler = Blip.ruler
    Row(
        horizontalArrangement = ruler.rowTight
    ) {
        for (source in sources.take(5)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ruler.round)
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