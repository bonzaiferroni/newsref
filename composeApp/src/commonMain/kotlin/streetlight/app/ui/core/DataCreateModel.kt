package streetlight.app.ui.core

abstract class DataCreateModel <Data, State: DataCreateState<Data>> (initialState: State)
    : UiModel<State>(initialState) {
        abstract fun createData()
}

interface DataCreateState <T> : UiState {
    val item: T
    val isFinished: Boolean
}