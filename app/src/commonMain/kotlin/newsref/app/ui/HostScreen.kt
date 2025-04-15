package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import io.pondlib.compose.ui.controls.*
import io.pondlib.compose.ui.nav.Scaffold

@Composable
fun HostScreen(
    route: HostRoute,
    viewModel: HostModel = viewModel { HostModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val host = state.host
    if (host == null) return
    Scaffold {
        TabCard() {
            Tab(name = "Articles", scrollable = false) {
                LazyColumn {
                    items(state.sources) {
                        PageLiteItem(it)
                    }
                }
            }
            Tab(name = "Feeds", scrollable = false, isVisible = state.feeds.isNotEmpty()) {
                val uriHandler = LocalUriHandler.current
                Column {
                    for (feed in state.feeds) {
                        Text(feed.url, modifier = Modifier.clickable { uriHandler.openUri(feed.url) })
                    }
                }
            }
        }
    }
}