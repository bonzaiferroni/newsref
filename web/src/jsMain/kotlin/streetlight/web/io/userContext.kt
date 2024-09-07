package streetlight.web.io

import io.kvision.core.Container
import streetlight.model.dto.UserInfo

fun Container.userContext(content: Container.(UserInfo) -> Unit) {

}