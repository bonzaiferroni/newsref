package newsref.web.ui.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import newsref.model.dto.EditUserRequest
import newsref.model.dto.PrivateInfo
import newsref.model.dto.UserInfo
import newsref.web.core.ViewModel
import newsref.web.io.client.globalApiClient
import newsref.web.io.stores.UserStore


class AccountModel(
    private val store: UserStore = UserStore(globalApiClient),
) : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
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

data class AccountState(
    val request: EditUserRequest = EditUserRequest(),
    val message: String = "",
    val deleteConfirm: Boolean = false,
)