package newsref.app.blip.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.nav.LocalNav
import newsref.app.blip.nav.NavRoute
import newsref.app.blip.theme.Blip
import newsref.app.utils.darken
import newsref.app.utils.modifyIfNotNull
import newsref.app.utils.modifyIfTrue

@Composable
fun Tabs(
    initialTab: String? = null,
    modifyRoute: ((String) -> NavRoute)? = null,
    modifier: Modifier = Modifier,
    viewModel: TabsModel = viewModel { TabsModel(initialTab) },
    content: @Composable TabScope.() -> Unit
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNav.current

    fun onTabChange(tab: String) {
        modifyRoute?.let {
            val navRoute = it(tab)
            nav.setRoute(navRoute)
        }
        viewModel.setTab(tab)
    }

    val scope = TabScope()
    scope.content()
    val tabs: List<TabItem> = scope.tabs
    if (tabs.isEmpty()) return

    val currentTab = tabs.firstOrNull() { it.name == state.tab }

    if (currentTab == null) {
        onTabChange(tabs.first().name)
        return
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row {
            for (tab in tabs) {
                if (!tab.isVisible) continue
                val (background, elevation) = when {
                    currentTab.name == tab.name -> Blip.localColors.surface to Blip.ruler.shadowElevation
                    else -> Blip.localColors.surface.darken() to 0.dp
                }
                Box(
                    modifier = Modifier
                        .modifyIfTrue(currentTab.name != tab.name) { Modifier.clickable { onTabChange(tab.name) } }
                        .shadow(elevation)
                        .background(background)
                        .padding(Blip.ruler.basePadding)
                        .weight(1f)
                ) {
                    Text(
                        text = tab.name,
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1
                    )
                }
            }
        }
        val scrollState = when {
            currentTab.scrollable -> rememberScrollState()
            else -> null
        }
        Surface(
            modifier = Modifier.fillMaxWidth()
                .modifyIfNotNull(scrollState) { verticalScroll(it) }
        ) {
            Column(
                modifier = Modifier.padding(Blip.ruler.innerPadding)
                    .fillMaxWidth()
            ) {
                currentTab.content()
            }
        }
    }
}

@Composable
fun TabScope.Tab(
    name: String,
    scrollable: Boolean = true,
    isVisible: Boolean = true,
    content: @Composable () -> Unit,
) {
    this.tabs.add(TabItem(
        name = name,
        scrollable = scrollable,
        isVisible = isVisible,
        content = content
    ))
}

internal data class TabItem(
    val name: String,
    val scrollable: Boolean = true,
    val isVisible: Boolean = true,
    val content: @Composable () -> Unit,
)

@Stable
class TabScope internal constructor() {
    internal val tabs = mutableListOf<TabItem>()
}