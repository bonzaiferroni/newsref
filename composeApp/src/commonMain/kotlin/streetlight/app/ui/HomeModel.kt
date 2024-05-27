package streetlight.app.ui

import cafe.adriel.voyager.core.model.screenModelScope
import streetlight.app.data.UserDao

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.app.ui.abstract.UiModel
import streetlight.app.ui.abstract.UiState
import streetlight.model.User

class HomeModel(private val userDao: UserDao) : UiModel<HomeState>(HomeState()) {

    fun growCounter() {
        sv = sv.copy(counter = sv.counter + 1)
    }

    fun fetchMessage() {
        screenModelScope.launch(Dispatchers.IO) {
            val response = userDao.fetchMessage()
            sv = sv.copy(message = response)
        }
    }
}

data class HomeState(
    val counter: Int = 0,
    val message: String = "",
    val user: User = User()
) : UiState