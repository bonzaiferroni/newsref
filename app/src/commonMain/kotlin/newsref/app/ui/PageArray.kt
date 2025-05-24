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
import pondui.ui.theme.Pond
import newsref.model.data.ChapterPageLite

@Composable
fun PageArray(pages: ImmutableList<ChapterPageLite>, color: Color) {
    val ruler = Pond.ruler
    Row(
        horizontalArrangement = ruler.rowUnit
    ) {
        for (chapterPage in pages) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(ruler.round)
                    .background(color)
            ) {
                chapterPage.page.imageUrl?.let {
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