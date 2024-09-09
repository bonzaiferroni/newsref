package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.web.core.ViewModel

class EditUserModel() : ViewModel() {
    val state = MutableStateFlow(EditUserState())
}

data class EditUserState(
    val name: String = "",
    val email: String = "",
    val venmo: String = "",
    val avatarUrl: String = "",
)