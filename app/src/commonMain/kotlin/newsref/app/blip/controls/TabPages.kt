package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import newsref.app.blip.theme.Blip
import newsref.app.utils.modifyIfTrue

@Composable
fun Tabs(
    currentPageName: String?,
    onChangePage: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable TabScope.() -> Unit
) {
    val scope = TabScope()
    scope.content()
    val tabs: List<TabItem> = scope.tabs
    if (tabs.isEmpty()) return

    val currentTab = tabs.firstOrNull() { it.name == currentPageName }

    if (currentTab == null) {
        onChangePage(tabs.first().name)
        return
    }

    Column(
        modifier = modifier
    ) {
        Row {
            for (tab in tabs) {
                if (!tab.isVisible) continue
                val (background, elevation) = when {
                    currentTab.name == tab.name -> Blip.localColors.surface to Blip.ruler.shadowElevation
                    else -> Blip.localColors.surface.copy(.8f) to 0.dp
                }
                Box(
                    modifier = Modifier
                        .modifyIfTrue(currentTab.name != tab.name) { Modifier.clickable { onChangePage(tab.name) } }
                        .shadow(elevation)
                        .background(background)
                        .padding(Blip.ruler.basePadding)
                        .weight(1f)
                ) {
                    Text(
                        text = tab.name,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        if (currentTab.scrollbar) {
            Text("Scrollbar not supported")
        } else {
            Surface() {
                Column(
                    modifier = Modifier.padding(Blip.ruler.innerPadding)
                ) {
                    currentTab.content()
                }
            }
        }
    }
}

@Composable
fun TabScope.Tab(
    name: String,
    scrollbar: Boolean = true,
    isVisible: Boolean = true,
    content: @Composable () -> Unit,
) {
    this.tabs.add(TabItem(
        name = name,
        scrollbar = scrollbar,
        isVisible = isVisible,
        content = content
    ))
}

internal data class TabItem(
    val name: String,
    val scrollbar: Boolean = true,
    val isVisible: Boolean = true,
    val content: @Composable () -> Unit,
)

@Stable
class TabScope internal constructor() {
    internal val tabs = mutableListOf<TabItem>()
}