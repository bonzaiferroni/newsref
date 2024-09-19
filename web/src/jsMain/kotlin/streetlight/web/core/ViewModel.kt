package streetlight.web.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.web.coScope

abstract class ViewModel {
    protected val viewModelScope: CoroutineScope
        get() = coScope
}

abstract class StateModel<T>(initialState: T) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    var state = _state.asStateFlow()
}