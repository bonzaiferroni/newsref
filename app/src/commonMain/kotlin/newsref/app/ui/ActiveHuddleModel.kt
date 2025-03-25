package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import newsref.app.blip.core.StateModel
import newsref.app.io.HuddleStore
import newsref.model.dto.*

class ActiveHuddleModel(
    val huddleId: Long,
    val store: HuddleStore = HuddleStore()
): StateModel<ActiveHuddleState>(ActiveHuddleState()) {

    fun refreshHuddle() {
        viewModelScope.launch {
            val content = store.readHuddleContent(huddleId)
            val responses = store.readHuddleResponses(huddleId).toImmutableList()
            setState { it.copy(content = content, responses = responses) }
        }
    }
}

data class ActiveHuddleState(
    val content: HuddleContentDto? = null,
    val responses: ImmutableList<HuddleResponseDto> = persistentListOf()
)