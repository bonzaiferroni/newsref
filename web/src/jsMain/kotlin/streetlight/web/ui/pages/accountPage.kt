package streetlight.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.link
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.launchedEffect
import streetlight.web.subscribe
import streetlight.web.ui.components.*
import streetlight.web.ui.models.AccountModel

fun Container.accountPage(context: AppContext): PortalEvents? {
    val model = AccountModel()
    userContext(context) { userInfo ->
        // console.log("editUserPage: userInfo=$userInfo")
        model.updateInfo(userInfo)
        rows() {
            ezForm("Account Settings") {
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

                rows(group = true) {
                    checkBox(label = "delete my data and log out") {
                        link("Read more", Pages.delete.route)
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

                row {
                    button("Back", style = ButtonStyle.SECONDARY).onClickLaunch {
                        context.navigate(Pages.user)
                    }
                    val button = button("Submit")
                    button.onClickLaunch {
                        val success = model.submit()
                        if (success) {
                            if (model.state.value.request.deleteUser) {
                                context.navigate(Pages.login)
                            } else {
                                context.navigate(Pages.user)
                            }
                        }
                    }
                    model.state.subscribe {
                        button.disabled = it.request.deleteUser && !it.deleteConfirm
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