package streetlight.app.ui.abstract

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel

abstract class UiModel<T: UiState>(
    initialState: T
) : ScreenModel {
    protected val _state = mutableStateOf(initialState)
    protected var sv
        get() = _state.value
        set(value) { _state.value = value }
    val state: State<T> = _state
}