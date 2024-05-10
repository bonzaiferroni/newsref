package com.bollwerks.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bollwerks.Food
import com.bollwerks.data.FoodDao
import com.bollwerks.utils.getBlankFood
import kotlinx.coroutines.launch

class FoodModel(
    private val foodDao: FoodDao,
) : ScreenModel {
    private val _state = mutableStateOf(FoodState())
    val state: State<FoodState> = _state

    init {
        screenModelScope.launch {
            foodDao.getAll().collect { foods ->
                _state.value = _state.value.copy(foods = foods)
            }
        }
    }

    fun onNameChange(name: String) {
        _state.value = state.value.copy(newFood = _state.value.newFood.copy(name = name))
    }

    fun onAddFood() {
        val newFood = state.value.newFood
        if (newFood.name.isNotBlank()) {
            screenModelScope.launch {
                foodDao.insert(newFood)
            }
            _state.value = state.value.copy(newFood = getBlankFood())
        }
    }
}

data class FoodState(
    val foods: List<Food> = emptyList(),
    val newFood: Food = getBlankFood(),
)