package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.*
import newsref.dashboard.*
import newsref.dashboard.ChartBoardRoute
import newsref.dashboard.ui.controls.ChartType
import newsref.app.pond.controls.Tab
import newsref.app.pond.controls.Tabs
import newsref.dashboard.ui.controls.TimeChart

@Composable
fun ChartBoardScreen(
    route: ChartBoardRoute,
    viewModel: ChartBoardModel = viewModel { ChartBoardModel(route) }
) {
    val state by viewModel.state.collectAsState()

    Tabs() {
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