package streetlight.app.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import streetlight.app.data.UserDao

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import streetlight.model.User

class HomeModel(private val userDao: UserDao) : ScreenModel {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    fun growCounter() {
        _state.value = _state.value.copy(counter = _state.value.counter + 1)
    }

    fun fetchMessage() {
        screenModelScope.launch(Dispatchers.IO) {
            val response = userDao.fetchMessage()
            _state.value = _state.value.copy(message = response)
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(user = _state.value.user.copy(name = name))
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(user = _state.value.user.copy(email = email))
    }

    fun addUser() {
        screenModelScope.launch(Dispatchers.IO) {
            val response = userDao.addUser(_state.value.user)
            _state.value = _state.value.copy(message = response)
        }
    }
}

data class HomeState(
    val counter: Int = 0,
    val message: String = "",
    val user: User = User()
)