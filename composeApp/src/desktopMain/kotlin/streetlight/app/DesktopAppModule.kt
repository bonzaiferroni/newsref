package streetlight.app

import streetlight.app.io.FoodDao

class DesktopAppModule : AppModule {
    private val db by lazy {
        PantryDb(
            driver = DatabaseDriverFactory().create()
        )
    }

    override fun provideExampleDataSource() = FoodDao(db)
}