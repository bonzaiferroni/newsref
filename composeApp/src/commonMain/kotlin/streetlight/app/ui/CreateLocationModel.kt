package streetlight.app.ui

import cafe.adriel.voyager.core.model.ScreenModel
import streetlight.model.Location

class CreateLocationModel : UiModel<CreateLocationState>(CreateLocationState()) {
}

data class CreateLocationState(
    val location: Location = Location(),
    val result: String = ""
) : UiState