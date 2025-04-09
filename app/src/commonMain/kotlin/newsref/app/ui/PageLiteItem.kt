package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import newsref.app.ChapterPageRoute
import newsref.app.PageRoute
import newsref.app.blip.controls.Button
import newsref.app.blip.controls.H4
import newsref.app.blip.controls.Label
import newsref.app.blip.controls.Text
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.theme.Blip
import newsref.model.data.PageLite
import newsref.model.utils.formatSpanLong

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageLiteItem(
    page: PageLite,
    chapterId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val nav = LocalNav.current
    Row(
        horizontalArrangement = Blip.ruler.rowTight,
        modifier = modifier
            .clickable {
                if (chapterId != null) {
                    nav.go(ChapterPageRoute(chapterId, page.id, page.headline))
                } else {
                    nav.go(PageRoute(page.id, page.headline))
                }
            }
            .padding(vertical = Blip.ruler.innerSpacing)
    ) {
        val color = Blip.colors.getSwatchFromIndex(page.id)
        ShapeImage(
            color = color,
            url = page.imageUrl,
            padding = PaddingValues(1.dp),
            modifier = Modifier.height(48.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Blip.ruler.rowTight
            ) {
                val uriHandler = LocalUriHandler.current
                H4(page.headline ?: "source: ${page.id}", maxLines = 2, modifier = Modifier.weight(1f))
                Button(
                    onClick = { uriHandler.openUri(page.url) },
                    background = Blip.colors.accent
                ) { Text("Read") }
            }
            FlowRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Label("${page.articleType.title} from ${page.existedAt.formatSpanLong()}, visibility: ${page.score}")
                Label(page.hostCore)
            }
        }
    }
}