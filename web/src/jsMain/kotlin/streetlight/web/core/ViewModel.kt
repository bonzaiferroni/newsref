package streetlight.web.core

import kotlinx.coroutines.CoroutineScope
import streetlight.web.coScope

abstract class ViewModel {
    protected val viewModelScope: CoroutineScope
        get() = coScope
}