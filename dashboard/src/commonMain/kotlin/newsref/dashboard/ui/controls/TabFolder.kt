package newsref.dashboard.ui.controls

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import newsref.dashboard.basePadding
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfPadding

@Composable
fun TabFolder(
    currentPageName: String,
    pages: List<TabPage>,
    onChangePage: (String) -> Unit,
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
                val background = when {
                    currentPage.name == page.name -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceDim
                }
                Box(
                    modifier = Modifier
                        .clickable { onChangePage(page.name) }
                        .background(background)
                        .padding(halfPadding)
                        .weight(1f)
                ) {
                    Text(
                        text = page.name,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        val scrollState = rememberScrollState()
//        val scrollbarColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        Box(

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(baseSpacing))
                currentPage.content()
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
    }
}

data class TabPage(
    val name: String,
    val content: @Composable () -> Unit,
)