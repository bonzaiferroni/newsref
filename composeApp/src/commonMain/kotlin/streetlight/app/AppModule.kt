package streetlight.app

import streetlight.app.data.FoodDao

interface AppModule {
    fun provideExampleDataSource(): FoodDao
}