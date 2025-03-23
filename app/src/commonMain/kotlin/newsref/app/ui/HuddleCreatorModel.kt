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

class HuddleCreatorModel(
    private val key: HuddleKey,
    private val store: HuddleStore = HuddleStore()
): StateModel<HuddleCreatorState>(HuddleCreatorState()) {

    init {
        viewModelScope.launch {
            val prompt = store.readPrompt(key)

            setState { it.copy(
                options = prompt.options.map { RadioOption(it.label, it.value) }.toImmutableList(),
                selectedValue = prompt.cachedValue,
                cachedValue = prompt.cachedValue
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
        viewModelScope.launch {

        }
    }
}

data class HuddleCreatorState(
    val selectedValue: String = "",
    val cachedValue: String = "",
    val commentText: String = "",
    val options: ImmutableList<RadioOption<String>> = persistentListOf()
) {
    val canSubmit get() = selectedValue != cachedValue && commentText.isNotBlank()
}