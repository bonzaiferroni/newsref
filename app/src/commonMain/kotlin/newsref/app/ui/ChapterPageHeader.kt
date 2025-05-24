package newsref.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import pondui.ui.controls.H2
import pondui.ui.theme.Pond
import pondui.ui.theme.ProvideSkyColors
import pondui.utils.rememberImmutableList
import newsref.model.data.Page
import newsref.model.data.ChapterPage
import newsref.model.data.Host
import newsref.model.data.Chapter

@Composable
fun ChapterPageHeader(
    height: Float,
    page: Page,
    chapterPage: ChapterPage,
    host: Host,
    chapter: Chapter
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
            verticalArrangement = Pond.ruler.columnUnit,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val color = Pond.colors.getSwatchFromIndex(chapterPage.chapterId)
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
                pages = chapter.pages?.rememberImmutableList()
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.shadow(Pond.ruler.shadowElevation, Pond.ruler.round)
                    .background(Pond.colors.secondary)
                    .clickable { println("open in browser") }
                    .padding(Pond.ruler.basePadding)
            ) {
                ProvideSkyColors {
                    H2(
                        text = "Read at\n${host.core}",
                        style = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}