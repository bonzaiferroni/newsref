package newsref.dashboard.ui.controls

import kotlinx.collections.immutable.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import newsref.app.pond.controls.Tab
import newsref.dashboard.*

@Composable
fun TabPages(
    currentPageName: String?,
    onChangePage: (String) -> Unit,
    pages: ImmutableList<Tab>,
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
        if (currentPage.scrollbar) {
            ScrollBox(
                modifier = Modifier.fillMaxSize()
            ) {
                currentPage.content()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(baseSpacing))
                currentPage.content()
            }
        }
    }
}
