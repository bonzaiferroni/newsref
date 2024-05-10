package com.bollwerks.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bollwerks.Food
import com.bollwerks.data.FoodDao
import com.bollwerks.data.UserDao
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        _state.value = _state.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun addUser() {
        screenModelScope.launch(Dispatchers.IO) {
            val response = userDao.addUser(_state.value.name, _state.value.email)
            _state.value = _state.value.copy(message = response)
        }
    }
}

data class HomeState(
    val counter: Int = 0,
    val message: String = "",
    val name: String = "",
    val email: String = "",
)