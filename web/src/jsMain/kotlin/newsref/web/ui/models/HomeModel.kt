package newsref.web.ui.models

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.model.dto.SourceInfo
import newsref.web.core.StateModel
import newsref.web.io.stores.SourceStore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class HomeModel(
	private val sourceStore: SourceStore = SourceStore(),
): StateModel<HomeState>(HomeState()) {
	private var sources by StateDelegate({it.sources}) { s, v -> s.copy(sources = v)}

	init {
		viewModelScope.launch {
			while (true) {
				refreshSources()
				console.log("HomeModel: refreshing sources")
				delay(5.minutes)
			}
		}
	}

	private suspend fun refreshSources() {
		 sources = sourceStore.getFeedSources().sortedByDescending { it.score }
	}
}

data class HomeState(
	val timeSpan: Duration = 7.days,
	val sources: List<SourceInfo> = emptyList(),
)