package com.bollwerks.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bollwerks.Food
import com.bollwerks.PantryDb
import kotlinx.coroutines.Dispatchers

class FoodDao(db: PantryDb) {
    private val queries = db.foodQueries

    //    Set id = null to let SQLDelight autogenerate the id
    fun insert(food: Food) {
        queries.insertFood(
            id = null,
            name = food.name,
            description = food.description,
            barcode = food.barcode,
            serving_size = food.serving_size,
            calories = food.calories,
            protein = food.protein,
            carbs = food.carbs,
            fat = food.fat,
            fiber = food.fiber,
        )
    }

    fun insertName(name: String) {
        queries.insertName(name = name)
    }

    // If you've added the coroutines extensions you'll be able to use asFlow()
    fun getAll() = queries.getAll().asFlow().mapToList(Dispatchers.IO)

    fun update(food: Food) {
        queries.update(
            id = food.id,
            name = food.name,
            description = food.description,
            barcode = food.barcode,
            serving_size = food.serving_size,
            calories = food.calories,
            protein = food.protein,
            carbs = food.carbs,
            fat = food.fat,
            fiber = food.fiber,
        )
    }

    fun delete(id: Long) {
        queries.delete(id = id)
    }
}