package streetlight.app.sql

import streetlight.app.Food
import streetlight.app.utils.getBlankFood
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import streetlight.app.ui.core.UiModel
import streetlight.app.ui.core.UiState

class FoodModel(
    private val foodDao: FoodDao,
) : UiModel<FoodState>(FoodState()) {

    init {
        viewModelScope.launch {
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
            viewModelScope.launch {
                foodDao.insert(newFood)
            }
            _state.value = state.value.copy(newFood = getBlankFood())
        }
    }
}

data class FoodState(
    val foods: List<Food> = emptyList(),
    val newFood: Food = getBlankFood(),
) : UiState