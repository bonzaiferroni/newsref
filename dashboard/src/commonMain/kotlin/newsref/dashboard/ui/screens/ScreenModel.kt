package newsref.dashboard.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ScreenModel<State>(initialState: State) : ViewModel() {
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected var sv by StateDelegate({ it }, { _, value -> value })

    inner class StateDelegate<Model: ScreenModel<State>, Value>(
        private val getter: (State) -> Value,
        private val setter: (State, Value) -> State
    ): ReadWriteProperty<Model, Value> {
        override fun getValue(thisRef: Model, property: KProperty<*>): Value {
            return getter(thisRef.state.value)
        }

        override fun setValue(thisRef: Model, property: KProperty<*>, value: Value) {
            thisRef._state.value = setter(thisRef.state.value, value)
        }
    }
}
