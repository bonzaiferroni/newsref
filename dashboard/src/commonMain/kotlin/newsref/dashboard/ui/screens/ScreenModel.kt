package newsref.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ScreenModel<State>(initialState: State) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()
    val stateNow get() = state.value

    protected fun setState(block: (State) -> State) {
        _state.value = block(state.value)
    }
}
