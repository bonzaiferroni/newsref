package newsref.web.ui.models

import kotlinx.coroutines.launch
import newsref.model.data.FeedSource
import newsref.web.core.StateModel
import newsref.web.io.stores.FeedSourceStore

class SourceModel(
	val id: Long,
	val feedSourceStore: FeedSourceStore = FeedSourceStore(),
): StateModel<SourceState>(SourceState()) {
	private var source by StateDelegate({it.source}) { s, v -> s.copy(source = v) }

	init {
		viewModelScope.launch {
			refreshSource()
		}
	}

	private suspend fun refreshSource() {
//		source = feedSourceStore.getSource(id)
	}
}

data class SourceState(
	val source: FeedSource? = null
)