package newsref.krawly.agents

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface KeyState {
    val valueMap: Map<String, Int>
}

open class KeyStateModule<State: KeyState>(initialState: KeyState): StateModule<KeyState>(initialState) {
    protected fun setState(key: String, value: Int) {
        // setState { it.co}
    }
}

open class StateModule<State>(initialState: State): CrawlerModule() {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()
    val stateNow get() = state.value

    protected fun setState(block: (State) -> State) {
        _state.value = block(state.value)
    }
}

open class CrawlerModule {
    val coroutineScope get () = CoroutineScope(Dispatchers.IO)
}

