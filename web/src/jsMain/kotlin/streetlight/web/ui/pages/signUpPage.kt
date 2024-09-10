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

    rows {
        h3("Sign up to Streetlight")

        // username
        row(group = true, alignItems = AlignItems.END) {
            flexGrow = 1
            text(label = "Username") {
                flexGrow = 1
                placeholder = "Username"
            }.bindTo(model::updateUsername)
            p().bindFrom(model.state) { state -> state.request.username.usernameError() }.mute()
        }

        // password
        rows(group = true) {
            row(group = true, alignItems = AlignItems.END) {
                rows(group = true) {
                    text(label = "Password") {
                        placeholder = "Password"
                        type = InputType.PASSWORD
                    }.bindTo(model::updatePassword)
                    text() {
                        placeholder = "Repeat password"
                        type = InputType.PASSWORD
                    }.bindTo(model::updateRepeatPassword)
                }
                rows(group = true) {
                    p().bindFrom(model.state) { state -> state.passwordMatchError() }.mute()
                    p().bindFrom(model.state) { state -> state.request.password.passwordLengthError() }.mute()
                    p().bindFrom(model.state) { state -> state.request.password.passwordCharsError() }.mute()
                }
            }
            p {
                +"Password should be at least 8 characters long and contain at least one letter, "
                +"number, a special character, eye of newt, two wishes, and a prayer."
            }.mute()
        }

        // email
        rows(group = true) {
            row(group = true) {
                text(label = "Email") {
                    placeholder = "Email (optional)"
                }.bindTo(model::updateEmail)
                // name
                text(label = "Name") {
                    placeholder = "Name (optional)"
                }.bindTo(model::updateName)
            }
            p {
                +"Your email address is securely stored and never shared. Read about "
                link("your privacy", "#/privacy")
                +" on Streetlight."
            }.mute()
        }

        // controls
        row(group = true) {
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

fun emoji(test: Boolean) = if (test) "💪" else "🙅"
fun String.usernameError() =
    "${emoji(validUsername)} ${if (validUsername) "Valid" else "Invalid"} username"
fun SignUpState.passwordMatchError() =
    "${emoji(passwordMatch)} Passwords ${if (!request.username.validUsername) "should" else ""} match"
fun String.passwordLengthError() =
    "${emoji(validPasswordLength)} At least 3 characters"
fun String.passwordCharsError() =
    "${emoji(validPassword)} Has required characters"