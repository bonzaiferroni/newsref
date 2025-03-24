package newsref.app.ui

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import newsref.app.blip.controls.RadioOption
import newsref.app.blip.core.StateModel
import newsref.app.io.HuddleStore
import newsref.model.data.HuddleKey
import newsref.model.data.HuddleSeed

class HuddleModel(
    private val key: HuddleKey,
    private val store: HuddleStore = HuddleStore()
): StateModel<HuddleCreatorState>(HuddleCreatorState()) {

    init {
        viewModelScope.launch {
            val prompt = store.readPrompt(key)

            setState { it.copy(
                options = prompt.options.map { RadioOption(it.label, it.value) }.toImmutableList(),
                selectedValue = prompt.cachedValue,
                cachedValue = prompt.cachedValue,
                guide = prompt.guide,
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
            val result = store.createHuddle(HuddleSeed(
                key = key,
                value = stateNow.selectedValue,
                comment = stateNow.commentText
            ))
            setState { it.copy(completed = result != null)}
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
    val completed: Boolean = false,
    val tab: String? = EDIT_TAB_NAME,
) {
    val canSubmit get() = selectedValue != cachedValue && commentText.isNotBlank()
}

const val EDIT_TAB_NAME = "Edit"