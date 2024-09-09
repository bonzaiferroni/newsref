package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.html.button
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.EditUserRequest
import streetlight.web.components.*
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.core.ViewModel
import streetlight.web.io.globalApiClient
import streetlight.web.io.stores.UserStore

fun Container.editUserPage(context: AppContext): PortalEvents? {
    val model = EditUserModel()
    rows {
        ezForm("Edit user") {
            ezText("Name").bindTo(model::updateName).bindFrom(model.state) { it.name }
            ezText("Email").bindTo(model::updateEmail).bindFrom(model.state) { it.email }
            ezText("Venmo").bindTo(model::updateVenmo).bindFrom(model.state) { it.venmo }
            ezText("Avatar URL").bindTo(model::updateAvatar).bindFrom(model.state) { it.avatarUrl }

            button("Submit") {
                onClickLaunch {
                    val success = model.submit()
                    if (success) {
                        context.routing.navigate("/user")
                    }
                }
            }
        }
    }
    return null
}

class EditUserModel(
    private val store: UserStore = UserStore(globalApiClient),
) : ViewModel() {
    private val _state = MutableStateFlow(EditUserRequest())
    val state = _state.asStateFlow()

    fun updateName(name: String) { _state.value = _state.value.copy(name = name) }
    fun updateEmail(email: String) { _state.value = _state.value.copy(email = email) }
    fun updateVenmo(venmo: String) { _state.value = _state.value.copy(venmo = venmo) }
    fun updateAvatar(avatarUrl: String) { _state.value = _state.value.copy(avatarUrl = avatarUrl) }

    suspend fun submit(): Boolean {
        return store.updateUser(state.value)
    }
}