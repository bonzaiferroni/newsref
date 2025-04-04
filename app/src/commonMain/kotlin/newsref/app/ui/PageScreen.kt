package newsref.app.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.PageRoute

@Composable
fun PageScreen(
    route: PageRoute,
    viewModel: PageModel = viewModel { PageModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val page = state.page
    val host = state.host
    if (page == null) return

    PageTabs(
        tab = state.tab,
        onChangeTab = viewModel::onChangeTab,
        page = page,
        host = host
    )
}