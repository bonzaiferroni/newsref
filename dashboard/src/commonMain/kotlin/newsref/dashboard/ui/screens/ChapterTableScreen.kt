package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import newsref.dashboard.*
import newsref.dashboard.ChapterItemRoute
import newsref.dashboard.ChapterTableRoute
import newsref.dashboard.LocalNavigator
import newsref.dashboard.ui.controls.CloudChart
import newsref.dashboard.ui.controls.SinceMenu
import newsref.dashboard.ui.table.*
import newsref.db.utils.format
import newsref.model.core.DataSort
import newsref.model.core.Sorting
import newsref.model.data.Chapter

@Composable
fun ChapterTableScreen(
    route: ChapterTableRoute,
    viewModel: ChapterTableModel = viewModel { ChapterTableModel()}
) {
    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(baseSpacing)
    ) {
        CloudChart(
            points = state.cloudPoints,
            height = 200.dp,
            onClickCloud = viewModel::selectId
        )

        SinceMenu(state.since, viewModel::changeSince)

        ChapterDataTable(
            chapters = state.chapters,
            changeSort = viewModel::changeSort,
            scrollId = state.selectedId,
        )
    }
}

@Composable
fun ChapterDataTable(
    chapters: ImmutableList<Chapter>,
    changeSort: (Sorting) -> Unit,
    scrollId: Long? = null,
    ) {
    val nav = LocalNavigator.current
    DataTable(
        name = "Chapters",
        items = chapters,
        onSorting = changeSort,
        scrollId = scrollId,
        isSelected = { id, item -> id == item.id },
        columnGroups = groups(
            columns(
                TableColumn(
                    name = "Score", width = 60, align = AlignCell.Right, sort = DataSort.Score
                ) { TextCell(it.score) },
                TableColumn(
                    name = "Title", weight = 1f,
                    onClickCell = { nav.go(ChapterItemRoute(it.id)) }
                ) { TextCell(it.title) },
            ),
            columns(
                TableColumn(
                    name = "HpnAt", width = 60, align = AlignCell.Right, sort = DataSort.Time
                ) { DurationAgoCell(it.happenedAt) },
                TableColumn(
                    name = "Size", width = 60, align = AlignCell.Right
                ) { TextCell(it.size) },
                TableColumn(
                    name = "Cohsn", width = 60, align = AlignCell.Right
                ) { TextCell(it.cohesion.format(2)) },
            )
        )
    )
}

