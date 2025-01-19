package newsref.dashboard.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
                    currentPage.name == page.name -> MaterialTheme.colorScheme.surfaceTint
                    else -> MaterialTheme.colorScheme.surfaceDim
                }
                Box(
                    modifier = Modifier
                        .background(background)
                        .weight(1f)
                        .clickable { onChangePage(page.name) }
                ) {
                    Text(page.name)
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            currentPage.content()
        }
    }
}

data class TabPage(
    val name: String,
    val content: @Composable () -> Unit,
)