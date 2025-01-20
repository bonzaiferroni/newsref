package newsref.web.ui.models

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.model.core.NewsSpan
import newsref.model.dto.SourceCollection
import newsref.model.dto.SourceInfo
import newsref.web.core.StateModel
import newsref.web.io.stores.SourceStore
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class HomeModel(
	private val sourceStore: SourceStore = SourceStore(),
) : StateModel<HomeState>(HomeState()) {
	private var sources by StateDelegate({ it.sources }) { s, v -> s.copy(sources = v) }

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
//		sources = sourceStore.getFeed(sv.newsSpan).sortedByDescending { it.score }
//		sv = sv.copy(refreshed = Clock.System.now())
	}

	fun changeSpan(newsSpan: NewsSpan) {
		sv = sv.copy(newsSpan = newsSpan)
		viewModelScope.launch {
			refreshSources()
		}
	}
}

data class HomeState(
	val newsSpan: NewsSpan = NewsSpan.DAY,
	val sources: List<SourceCollection> = emptyList(),
	val refreshed: Instant = Instant.DISTANT_PAST
)