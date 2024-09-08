package streetlight.app.ui.debug

import streetlight.app.io.UserDao

import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.core.User

class DebugModel(private val userDao: UserDao) : UiModel<DebugState>(DebugState()) {

    fun growCounter() {
        sv = sv.copy(counter = sv.counter + 1)
    }
}

data class DebugState(
    val counter: Int = 0,
    val message: String = "",
    val user: User = User()
) : UiState