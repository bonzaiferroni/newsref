package streetlight.app

import streetlight.app.io.FoodDao

interface AppModule {
    fun provideExampleDataSource(): FoodDao
}