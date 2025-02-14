package newsref.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import newsref.app.blip.core.StateModel
import newsref.dashboard.ChartBoardRoute
import newsref.dashboard.ui.controls.*
import newsref.db.models.ChapterFinderLog
import newsref.db.services.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ChartBoardModel(
    route: ChartBoardRoute,
    val dataLogService: DataLogService = DataLogService()
) : StateModel<ChartBoardState>(ChartBoardState(route.page)) {

    init {
        viewModelScope.launch {
            while (true) {
                refreshData()
                delay(1.minutes)
            }
        }
    }

    fun onChangePage(page: String) {
        setState { it.copy(page = page) }
    }

    private suspend fun refreshData() {
        val data = dataLogService.readJsons(ChapterFinderLog.state, Clock.System.now() - 7.days)

        val bucketData = data.toTimeChartData(
            getTime = { it.time },
            buckets = 20,
            createTimeSeries("exclusions") { it.value.exclusions.toFloat() },
            createTimeSeries("buckets") { it.value.buckets.toFloat() },
        )

        val signalData = data.toTimeChartData(
            getTime = { it.time },
            buckets = 20,
            createTimeSeries("primarySignals", true) { it.value.primarySignals.toFloat() },
            createTimeSeries("secondarySignals", true) { it.value.secondarySignals.toFloat() },
        )

        val vectorData = data.toTimeChartData(
            getTime = { it.time },
            buckets = 20,
            createTimeSeries("emptySignals", true) { it.value.emptySignals.toFloat() },
            createTimeSeries("vectorsFetched", true) { it.value.vectorsFetched.toFloat() },
        )

        val chapterCountData = data.toTimeChartData(
            getTime = { it.time },
            buckets = 20,
            createTimeSeries("chapters") { it.value.chapters.toFloat() },
        )


        val chapterData = data.toTimeChartData(
            getTime = { it.time },
            buckets = 20,
            createTimeSeries("chaptersCreated", true) { it.value.chaptersCreated.toFloat() },
            createTimeSeries("chaptersUpdated", true) { it.value.chaptersUpdated.toFloat() },
            createTimeSeries("chaptersDeleted", true) { it.value.chaptersDeleted.toFloat() },
        )

        setState { it.copy(
            bucketData = bucketData,
            signalData = signalData,
            vectorData = vectorData,
            chapterCountData = chapterCountData,
            chapterData = chapterData,
        ) }
    }
}

data class ChartBoardState(
    val page: String?,
    val bucketData: TimeChartData? = null,
    val signalData: TimeChartData? = null,
    val vectorData: TimeChartData? = null,
    val chapterCountData: TimeChartData? = null,
    val chapterData: TimeChartData? = null,
)