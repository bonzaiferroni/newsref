package newsref.web.ui.models

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import newsref.model.data.FeedSource
import newsref.model.dto.SourceInfo
import newsref.web.core.StateModel
import newsref.web.io.stores.FeedSourceStore
import kotlin.time.Duration.Companion.minutes

class HomeModel(
	private val feedSourceStore: FeedSourceStore = FeedSourceStore(),
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
		 sources = feedSourceStore.getFeedSources().sortedByDescending { it.score }
	}
}

data class HomeState(
	val sources: List<SourceInfo> = emptyList()
)