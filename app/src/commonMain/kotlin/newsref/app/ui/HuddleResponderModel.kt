package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.app.blip.controls.RadioOption
import newsref.app.blip.core.StateModel
import newsref.app.io.HuddleStore
import newsref.model.data.HuddleKey
import newsref.model.data.HuddleResponseSeed

class HuddleResponderModel(
    private val key: HuddleKey,
    private val store: HuddleStore = HuddleStore()
): StateModel<HuddleCreatorState>(HuddleCreatorState()) {

    init {
        viewModelScope.launch {
            val prompt = store.readPrompt(key)

            val userResponseId = prompt.activeId?.let {
                store.readUserResponseId(it)
            }

            setState { it.copy(
                options = prompt.options.map { RadioOption(it.label, it.value) }.toImmutableList(),
                selectedValue = prompt.cachedValue,
                cachedValue = prompt.cachedValue,
                guide = prompt.guide,
                activeId = prompt.activeId,
                userResponseId = userResponseId,
                tab = when {
                    userResponseId == null -> EDIT_TAB_NAME
                    else -> HUDDLE_TAB_NAME
                },
            )}
        }
    }

    fun selectValue(value: String) {
        setState { it.copy(selectedValue = value) }
    }

    fun setCommentText(text: String) {
        setState { it.copy(commentText = text) }
    }

    fun submit() {
        if (!stateNow.canSubmit) return
        viewModelScope.launch {
            val response = store.createHuddle(HuddleResponseSeed(
                key = key,
                value = stateNow.selectedValue,
                comment = stateNow.commentText,
            ))
            setState { it.copy(
                userResponseId = response.responseId,
                activeId = response.huddleId,
                tab = HUDDLE_TAB_NAME
            ) }
        }
    }

    fun changeTab(tab: String?) {
        setState { it.copy(tab = tab) }
    }
}

data class HuddleCreatorState(
    val selectedValue: String = "",
    val cachedValue: String = "",
    val commentText: String = "",
    val guide: String = "",
    val options: ImmutableList<RadioOption<String>> = persistentListOf(),
    val tab: String? = EDIT_TAB_NAME,
    val activeId: Long? = null,
    val userResponseId: Long? = null,
) {
    val canSubmit get() = selectedValue != cachedValue && commentText.isNotBlank()
}

const val EDIT_TAB_NAME = "Edit"
const val HUDDLE_TAB_NAME = "Huddle"