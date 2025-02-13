package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.*
import newsref.dashboard.*
import newsref.dashboard.ui.controls.ChartType
import newsref.dashboard.ui.controls.TabPage
import newsref.dashboard.ui.controls.TabPages
import newsref.dashboard.ui.controls.TimeChart
import newsref.dashboard.ui.controls.pages

@Composable
fun ChartBoardScreen(
    route: ChartBoardRoute,
    viewModel: ChartBoardModel = viewModel { ChartBoardModel(route) }
) {
    val state by viewModel.state.collectAsState()

    TabPages(
        state.page,
        viewModel::onChangePage,
        pages = pages(
            TabPage("Chapter Finder") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(baseSpacing)
                ) {
                    state.bucketData?.let { TimeChart(it, type = ChartType.Area) }
                    state.signalData?.let { TimeChart(it, type = ChartType.Area) }
                    state.vectorData?.let { TimeChart(it, type = ChartType.Area) }
                    state.chapterCountData?.let { TimeChart(it, type = ChartType.Area) }
                    state.chapterData?.let { TimeChart(it, type = ChartType.Area) }
                }
            }
        )
    )
}