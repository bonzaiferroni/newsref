package com.bollwerks.utils

import com.bollwerks.Food

fun getBlankFood() = Food(
    id = 0,
    name = "",
    description = "",
    barcode = "",
    serving_size = 0.0,
    calories = 0.0,
    protein = 0.0,
    carbs = 0.0,
    fat = 0.0,
    fiber = 0.0
)