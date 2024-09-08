package streetlight.web.pages

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.onClickLaunch
import io.kvision.form.text.text
import io.kvision.html.*
import streetlight.web.components.*
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents

fun Container.signUpPage(context: AppContext): PortalEvents? {
    val model = SignUpModel()
    fun emoji(test: Boolean) = if (test) "🙆" else "🙅"

    rows {
        h3("Create User")

        // username
        row(group = true, alignItems = AlignItems.END) {
            flexGrow = 1
            text(label = "Username") {
                flexGrow = 1
                placeholder = "Username"
            }.bindTo(model::updateUsername)
            p().bindFrom(model.state) {
                "${emoji(it.info.validUsername)} ${if (it.info.validUsername) "Valid" else "Invalid"} username"
            }.mute()
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
                    p().bindFrom(model.state) {
                        "${emoji(it.passwordMatch)} Passwords ${if (!it.info.validUsername) "should" else ""}  match"
                    }.mute()
                    p().bindFrom(model.state) { "${emoji(it.info.validPasswordLength)} At least 3 characters" }.mute()
                    p().bindFrom(model.state) { "${emoji(it.info.validPassword)} Has required characters" }.mute()
                }
            }
            p {
                +"Password should be at least 8 characters long and "
                +"contain at least one letter, number, and special character."
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
            button("Back", style = ButtonStyle.SECONDARY) {
                onClick {
                    context.routing.navigate("/login")
                }
            }
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