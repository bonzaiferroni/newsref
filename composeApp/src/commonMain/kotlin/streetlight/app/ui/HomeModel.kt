package streetlight.app.ui

import streetlight.app.data.UserDao

import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState
import streetlight.model.User

class HomeModel(private val userDao: UserDao) : UiModel<HomeState>(HomeState()) {

    fun growCounter() {
        sv = sv.copy(counter = sv.counter + 1)
    }
}

data class HomeState(
    val counter: Int = 0,
    val message: String = "",
    val user: User = User()
) : UiState