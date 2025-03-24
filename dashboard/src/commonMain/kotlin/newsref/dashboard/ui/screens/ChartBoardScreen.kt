package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.*
import newsref.dashboard.*
import newsref.dashboard.ChartBoardRoute
import newsref.dashboard.ui.controls.ChartType
import newsref.app.blip.controls.Tab
import newsref.app.blip.controls.Tabs
import newsref.app.blip.controls.rememberTabs
import newsref.dashboard.ui.controls.TimeChart

@Composable
fun ChartBoardScreen(
    route: ChartBoardRoute,
    viewModel: ChartBoardModel = viewModel { ChartBoardModel(route) }
) {
    val state by viewModel.state.collectAsState()

    Tabs(
        state.page,
        viewModel::onChangePage,
    ) {
        Tab("Chapter Finder") {
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
    }
}