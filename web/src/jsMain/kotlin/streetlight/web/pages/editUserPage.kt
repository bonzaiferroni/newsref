package streetlight.web.pages

import io.kvision.core.Container
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h3
import streetlight.web.components.*
import streetlight.web.core.AppContext
import streetlight.web.core.PortalEvents
import streetlight.web.io.userContext

fun Container.editUserPage(context: AppContext): PortalEvents? {
    val model = EditUserModel()
    suspend fun submit(state: EditUserState) {
//        val success = model.submit()
//        if (success) {
//            context.routing.navigate("/user")
//        }
    }
    userContext(context) { userInfo ->
        rows {
            ezForm("Edit user", onSubmit = ::submit) {
                ezText("Name", { value, state -> state?.copy(name = value) }, { it?.name })
                ezText("Email", { value, state -> state?.copy(email = value) }, { it?.email })
                ezText("Venmo", { value, state -> state?.copy(venmo = value) }, { it?.venmo })
                ezText("Avatar", { value, state -> state?.copy(avatarUrl = value) }, { it?.avatarUrl })
            }
        }
    }
    return null
}