package com.bollwerks

import com.bollwerks.AppModule
import com.bollwerks.DatabaseDriverFactory
import com.bollwerks.PantryDb
import com.bollwerks.data.FoodDao

class DesktopAppModule : AppModule {
    private val db by lazy {
        PantryDb(
            driver = DatabaseDriverFactory().create()
        )
    }

    override fun provideExampleDataSource() = FoodDao(db)
}