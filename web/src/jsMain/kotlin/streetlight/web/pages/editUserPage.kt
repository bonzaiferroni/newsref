package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.html.button
import io.kvision.html.link
import streetlight.web.components.*
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.components.userContext
import streetlight.web.launchedEffect
import streetlight.web.subscribe

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
                    val checkBox = checkBox(label = "I understand this action is permanent") {
                        transition = Transition("opacity", 0.2)
                    }
                        .bindFrom(model.state) { it.deleteConfirm }
                        .bindTo(model::updateDeleteConfirm)
                    model.state.subscribe {
                        checkBox.opacity = it.request.deleteUser.run { if (this) 1.0 else 0.0 }
                    }
                }

                val button = button("Submit") {
                    onClickLaunch {
                        val success = model.submit()
                        if (success) {
                            if (model.state.value.request.deleteUser) {
                                context.routing.navigate("/login")
                            } else {
                                context.routing.navigate("/user")
                            }
                        }
                    }
                }
                model.state.subscribe {
                    button.disabled = it.request.deleteUser && !it.deleteConfirm
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