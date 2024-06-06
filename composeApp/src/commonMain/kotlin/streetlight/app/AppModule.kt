package streetlight.app

import streetlight.app.sql.FoodDao

interface AppModule {
    fun provideExampleDataSource(): FoodDao
}