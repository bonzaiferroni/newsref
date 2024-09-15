package streetlight.web.ui.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.*
import streetlight.model.utils.validPassword
import streetlight.model.utils.validPasswordLength
import streetlight.model.utils.validUsername
import streetlight.web.core.AppContext
import streetlight.web.core.Pages
import streetlight.web.core.PortalEvents
import streetlight.web.core.navigate
import streetlight.web.ui.components.*
import streetlight.web.ui.models.SignUpModel
import streetlight.web.ui.models.SignUpState

fun Container.signUpPage(context: AppContext): PortalEvents? {
    val model = SignUpModel()

    col {
        h3("Sign up to Streetlight")

        // username
        row(alignItems = AlignItems.END) {
            text(label = "Username") {
                flexGrow = 1
                placeholder = "Username"
            }.bindTo(model::updateUsername)
            fun showMsg(state: SignUpState) = state.request.username.isNotBlank()
            validMsg(model.state, "At least 3 characters", ::showMsg) { it.request.username.validUsername }
        }

        // password
        col {
            row(alignItems = AlignItems.END) {
                col(alignItems = AlignItems.STRETCH) {
                    text(label = "Password") {
                        placeholder = "Password"
                        type = InputType.PASSWORD
                    }.bindTo(model::updatePassword)
                    text() {
                        placeholder = "Repeat password"
                        type = InputType.PASSWORD
                    }.bindTo(model::updateRepeatPassword)
                }
                fun showMsg(state: SignUpState) = state.request.password.isNotBlank()
                col {
                    validMsg(model.state, "At least 3 characters", ::showMsg)
                    { it.request.password.validPasswordLength }
                    validMsg(model.state, "Has variety of characters", ::showMsg)
                    { it.request.password.validPassword }
                    validMsg(model.state, "Repeat password", ::showMsg) { it.passwordMatch }
                }
            }
            p {
                +"Password strength: At least 8 characters long and should have a letter, "
                +"number, a special character, eye of newt, two wishes, and a prayer."
            }.mute()
        }

        // email
        col {
            row {
                text(label = "Email") { placeholder = "Email (optional)" }.bindTo(model::updateEmail)
                text(label = "Name") { placeholder = "Name (optional)" }.bindTo(model::updateName)
            }
            p {
                +"Your email address is securely stored and never shared. Read about "
                link("your privacy", Pages.privacy)
                +" on Streetlight."
            }.mute()
        }

        // controls
        row {
            button("Back", style = ButtonStyle.SECONDARY).onClick { context.navigate(Pages.login) }
            button("Create") {
                disabled = true
                onClickLaunch {
                    if (model.signUp()) {
                        context.routing.navigate("/user")
                    }
                }
            }.bindWidgetFrom(model.state) {
                disabled = !it.validSignUp
            }
            p().bindFrom(model.state) { it.resultMessage }
        }
    }
    return null
}