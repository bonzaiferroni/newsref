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
import newsref.model.data.ChapterPageLite
import newsref.model.data.PageLite

@Composable
fun PageArray(pages: ImmutableList<ChapterPageLite>, color: Color) {
    val ruler = Blip.ruler
    Row(
        horizontalArrangement = ruler.rowTight
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