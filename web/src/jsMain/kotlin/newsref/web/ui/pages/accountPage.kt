package newsref.web.ui.pages

import io.kvision.core.Container
import io.kvision.core.Transition
import io.kvision.core.onClickLaunch
import io.kvision.form.check.checkBox
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h1
import newsref.web.core.AppContext
import newsref.web.core.Pages
import newsref.web.core.PortalEvents
import newsref.web.core.navigate
import newsref.web.launchedEffect
import newsref.web.subscribe
import newsref.web.ui.components.*
import newsref.web.ui.models.AccountModel

fun Container.accountPage(context: AppContext): PortalEvents? {
    val model = AccountModel()
    userContext(context) { userInfo ->
        // console.log("editUserPage: userInfo=$userInfo")
        model.updateInfo(userInfo)
        col {
            h1 {
                +"Account Settings"
            }
            ezForm {
                col(group = true) {
                    ezText("Name").bindFrom(model.state) { it.request.name }.bindTo(model::updateName)
                    checkBox(label = "delete name")
                        .bindFrom(model.state) { it.request.deleteName }
                        .bindTo(model::updateDeleteName)
                }
                col(group = true) {
                    ezText("Email").bindTo(model::updateEmail).bindFrom(model.state) { it.request.email }
                    checkBox(label = "delete email")
                        .bindFrom(model.state) { it.request.deleteEmail }
                        .bindTo(model::updateDeleteEmail)
                }
                ezText("Venmo").bindTo(model::updateVenmo).bindFrom(model.state) { it.request.venmo }
                ezText("Avatar URL").bindTo(model::updateAvatar).bindFrom(model.state) { it.request.avatarUrl }

                col(group = true) {
                    row(group = true) {
                        checkBox(label = "Delete my data and log out. ")
                            .bindFrom(model.state) { it.request.deleteUser }
                            .bindTo(model::updateDeleteUser)
                        link("read more", Pages.privacy)
                    }
                    val checkBox = checkBox(label = "I understand this action is permanent.") {
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