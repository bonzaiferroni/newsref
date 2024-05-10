package com.bollwerks

import com.bollwerks.data.FoodDao

interface AppModule {
    fun provideExampleDataSource(): FoodDao
}