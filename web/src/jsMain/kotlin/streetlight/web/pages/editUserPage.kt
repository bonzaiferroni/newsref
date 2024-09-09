package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.form.check.radio
import io.kvision.form.check.radioGroup
import io.kvision.html.button
import io.kvision.html.link
import io.kvision.state.bindTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import streetlight.model.dto.EditUserRequest
import streetlight.model.dto.PrivateInfo
import streetlight.model.dto.UserInfo
import streetlight.web.components.*
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.core.ViewModel
import streetlight.web.io.globalApiClient
import streetlight.web.io.stores.UserStore
import streetlight.web.io.userContext
import streetlight.web.launchedEffect

fun Container.editUserPage(context: AppContext): PortalEvents? {
    val model = EditUserModel()
    userContext(context) { userInfo ->
        // console.log("editUserPage: userInfo=$userInfo")
        model.updateInfo(userInfo)
        rows() {
            ezForm("Edit user") {
                rows(group = true) {
                    ezText("Name").bindFrom(model.state) { it.request.name }.bindTo(model::updateName)
                    checkBox(label = "delete name")
                        .bindFrom(model.state) { it.request.deleteName }
                        .bindTo(model::updateDeleteName)
                }
                rows(group = true) {
                    ezText("Email").bindTo(model::updateEmail).bindFrom(model.state) { it.request.email }
                    checkBox(label = "delete email")
                        .bindFrom(model.state) { it.request.deleteEmail }
                        .bindTo(model::updateDeleteEmail)
                }
                ezText("Venmo").bindTo(model::updateVenmo).bindFrom(model.state) { it.request.venmo }
                ezText("Avatar URL").bindTo(model::updateAvatar).bindFrom(model.state) { it.request.avatarUrl }

                row {
                    checkBox(label = "delete my data and log out") {
                        link("Read more", "/privacy/delete") //
                    }
                        .bindFrom(model.state) { it.request.deleteUser }
                        .bindTo(model::updateDeleteUser)
                    checkBox(label = "I understand this action is permanent")
                        .bindFrom(model.state) { it.request.deleteUser }
                        .bindTo(model::updateDeleteUser)
                }

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

        launchedEffect {
            val privateInfo = model.getPrivateInfo()
            // console.log("editUserPage: privateInfo=$privateInfo")
            model.updateName(privateInfo.name ?: "")
            model.updateEmail(privateInfo.email ?: "")
        }
    }
    return null
}