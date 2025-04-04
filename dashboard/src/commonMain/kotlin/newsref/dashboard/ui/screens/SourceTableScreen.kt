package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.viewmodel.compose.*
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import newsref.dashboard.LocalNavigator
import newsref.dashboard.PageItemRoute
import newsref.dashboard.PageTableRoute
import newsref.dashboard.ui.table.*
import newsref.dashboard.utils.*
import newsref.model.data.DataSort
import newsref.model.data.PageInfo
import newsref.model.data.Sorting

@Composable
fun PageTableScreen(
    route: PageTableRoute,
    viewModel: PageTableModel = viewModel { PageTableModel() }
) {
    val state by viewModel.state.collectAsState()
    PropertyTable(
        name = "Page Table",
        item = state,
        properties = listOf(
            PropertyRow("count") {
                val value = if (state.paused)
                    "${state.count} (+${state.count - state.countShown})" else "${state.count}"
                TextCell(value)
            }
        )
    )
    Row {
        IconButton(onClick = viewModel::togglePause) {
            val icon = when (state.paused) {
                true -> TablerIcons.PlayerPlay
                false -> TablerIcons.PlayerPause
            }
            Icon(imageVector = icon, contentDescription = "Play/Pause")
        }
        Button(onClick = { }) {
            Text("Button")
        }
    }
    PageTable(
        pages = state.items,
        searchText = state.searchText,
        onSearch = viewModel::onSearch,
        isNew = { it.pageId > state.previousTopId},
        onFirstVisibleIndex = viewModel::trackIndex,
    )
}

@Composable
fun PageTable(
    pages: List<PageInfo>,
    searchText: String = "",
    scrollId: Long? = null,
    onSearch: ((String) -> Unit)? = null,
    onSorting: ((Sorting) -> Unit)? = null,
    isNew: ((PageInfo) -> Boolean )? = null,
    onFirstVisibleIndex: ((Int) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val nav = LocalNavigator.current
    DataTable(
        name = "Sources",
        items = pages,
        isNew = isNew,
        onSorting = onSorting,
        searchText = searchText,
        onSearch = onSearch,
        scrollId = scrollId,
        isSelected = { index, item -> index == item.pageId},
        getKey = { it.pageId },
        glowFunction = { glowOverMin(it.seenAt) },
        onFirstVisibleIndex = onFirstVisibleIndex,
        columnGroups = listOf(
            columns(
                TableColumn("Scr", 40, alpha = .8f, sort = DataSort.Score) { TextCell(it.score) },
                TableColumn<PageInfo>("headline", weight = 1f) { TextCell(it.headline ?: it.pageTitle) }
                    .onClick(ToolTip("Go to source", TipType.Action)) { nav.go(PageItemRoute(it.pageId)) },
            ),
            columns(
                TableColumn<PageInfo>("url", alpha = .8f) { TextCell(it.url) }
                    .onClick(ToolTip("Open in browser", TipType.Action)) { uriHandler.openUri(it.url) }
                    .addControl(TablerIcons.Copy, ToolTip("Copy Url", TipType.Action)) { clipboardManager.setRawText(it.url) },
            ),
            columns(
                TableColumn<PageInfo>("Host", weight = 1f) { TextCell(it.hostName ?: it.hostCore) }
                    .onClick(ToolTip("Go to host")) { /* todo: open host */ },
                TableColumn("Words", 60, AlignCell.Right) { NumberCell(it.wordCount) },
                TableColumn("Ds", 30, headerTip = ToolTip("Description")) { EmojiCell("📃", it.description) },
                TableColumn("HL", 30, headerTip = ToolTip("Host Logo")) { EmojiCell("💈", it.hostLogo) },
                TableColumn("Im", 30, headerTip = ToolTip("Featured Image")) { EmojiCell("🖼", it.image) },
                TableColumn("Th", 30, headerTip = ToolTip("Thumbnail")) { EmojiCell("💅", it.thumbnail) },
                TableColumn("pub", 60, AlignCell.Right) { DurationAgoCell(it.publishedAt) },
            ),
            columns(
                TableColumn("Section", weight = 1f, alpha = .8f) { TextCell(it.metaSection ?: "") },
                TableColumn("", 60) { },
                TableColumn("seen", 60, AlignCell.Right, .8f, sort = DataSort.Time) { DurationAgoCell(it.seenAt) }
            )
        )
    )
}
