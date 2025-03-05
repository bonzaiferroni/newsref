package newsref.app.blip.controls

import kotlinx.collections.immutable.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip
import newsref.app.utils.modifyIfTrue

@Composable
fun TabPages(
    currentPageName: String?,
    onChangePage: (String) -> Unit,
    pages: ImmutableList<TabPage>,
    modifier: Modifier = Modifier
) {
    if (pages.isEmpty()) return

    val currentPage = pages.firstOrNull() { it.name == currentPageName }

    if (currentPage == null) {
        onChangePage(pages.first().name)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row {
            for (page in pages) {
                if (!page.isVisible) continue
                val (background, elevation) = when {
                    currentPage.name == page.name -> Blip.localColors.surface to Blip.ruler.shadowElevation
                    else -> Blip.localColors.surface.copy(.8f) to 0.dp
                }
                Box(
                    modifier = Modifier
                        .modifyIfTrue(currentPage.name != page.name) { Modifier.clickable { onChangePage(page.name) } }
                        .shadow(elevation)
                        .background(background)
                        .padding(Blip.ruler.basePadding)
                        .weight(1f)
                ) {
                    Text(
                        text = page.name,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        if (currentPage.scrollbar) {
            Text("Scrollbar not supported")
        } else {
            Surface() {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(Blip.ruler.innerPadding)
                ) {
                    currentPage.content()
                }
            }
        }
    }
}

data class TabPage(
    val name: String,
    val scrollbar: Boolean = true,
    val isVisible: Boolean = true,
    val content: @Composable () -> Unit,
)

fun pages(vararg elements: TabPage) = elements.toImmutableList()
