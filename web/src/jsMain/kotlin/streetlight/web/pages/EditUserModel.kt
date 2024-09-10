package streetlight.web.pages

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.EditUserRequest
import streetlight.model.dto.PrivateInfo
import streetlight.model.dto.UserInfo
import streetlight.web.core.ViewModel
import streetlight.web.io.client.globalApiClient
import streetlight.web.io.stores.UserStore


class EditUserModel(
    private val store: UserStore = UserStore(globalApiClient),
) : ViewModel() {
    private val _state = MutableStateFlow(EditUserState())
    val state = _state.asStateFlow()

    private var request: EditUserRequest
        get() = state.value.request
        set(value) { _state.value = state.value.copy(request = value) }

    fun updateName(name: String) { request = request.copy(name = name)}
    fun updateEmail(email: String) { request = request.copy(email = email) }
    fun updateVenmo(venmo: String) { request = request.copy(venmo = venmo) }
    fun updateAvatar(avatarUrl: String) { request = request.copy(avatarUrl = avatarUrl) }
    fun updateDeleteName(delete: Boolean) { request = request.copy(deleteName = delete) }
    fun updateDeleteEmail(delete: Boolean) { request = request.copy(deleteEmail = delete) }
    fun updateDeleteUser(delete: Boolean) { request = request.copy(deleteUser = delete) }
    fun updateDeleteConfirm(confirm: Boolean) { _state.value = state.value.copy(deleteConfirm = confirm) }

    suspend fun getPrivateInfo(): PrivateInfo { return store.getPrivateInfo() }
    suspend fun submit(): Boolean { return store.updateUser(request) }

    fun updateInfo(userInfo: UserInfo) {
        request = request.copy(avatarUrl = userInfo.avatarUrl ?: "", venmo = userInfo.venmo ?: "",)
    }
}

data class EditUserState(
    val request: EditUserRequest = EditUserRequest(),
    val message: String = "",
    val deleteConfirm: Boolean = false,
)