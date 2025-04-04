package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import newsref.app.blip.controls.H2
import newsref.app.blip.theme.Blip
import newsref.app.blip.theme.ProvideSkyColors
import newsref.model.data.Page
import newsref.model.data.ChapterPack
import newsref.model.data.ChapterPage
import newsref.model.data.Host
import newsref.app.utils.rememberImmutableList

@Composable
fun ChapterPageHeader(
    height: Float,
    page: Page,
    chapterPage: ChapterPage,
    host: Host,
    chapter: ChapterPack
) {
    Card(
        shape = RoundedCornerShape(
            topStart = height / 2,
            topEnd = height / 2,
            bottomStart = 0f,
            bottomEnd = 0f
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Blip.ruler.columnTight
        ) {
            val color = Blip.colors.getSwatchFromIndex(chapterPage.chapterId)
            BalloonHeader(
                color = color,
                title = page.bestTitle,
                imageUrl = page.imageUrl,
                score = page.score ?: 0,
                height = height,
                isSelected = false,
                onSelect = { },
                storyCount = null,
                time = page.existedAt,
                sources = chapter.pageBits.rememberImmutableList()
            )
            Row(
                horizontalArrangement = Blip.ruler.rowSpaced,
                modifier = Modifier.height(height.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(height.dp)
                ) {

                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxHeight()
                        .weight(1f)
                        .shadow(Blip.ruler.shadowElevation, RoundedCornerShape(height / 2))
                        .background(Blip.colors.accent)
                        .clickable { println("open in browser") }
                ) {
                    ProvideSkyColors {
                        H2(
                            text = "Read at\n${host.core}",
                            style = TextStyle(textAlign = TextAlign.Center)
                        )
                    }
                }
                Column(
                    modifier = Modifier.size(height.dp)
                ) {

                }
            }
        }
    }
}