package streetlight.app

import org.koin.dsl.module
import streetlight.app.io.ApiClient
import streetlight.app.io.AreaDao
import streetlight.app.io.EventDao
import streetlight.app.io.LocationDao
import streetlight.app.io.UserDao
import streetlight.app.sql.FoodDao
import streetlight.app.sql.FoodModel
import streetlight.app.ui.HomeModel
import streetlight.app.ui.data.AreaCreatorModel
import streetlight.app.ui.data.AreaListModel
import streetlight.app.ui.data.EventCreatorModel
import streetlight.app.ui.data.EventListModel
import streetlight.app.ui.data.LocationCreatorModel
import streetlight.app.ui.data.LocationListModel
import streetlight.app.ui.data.UserCreatorModel
import streetlight.app.ui.login.LoginModel

val myModule = module {
    // your dependencies here
    single { PantryDb(driver = DatabaseDriverFactory().create()) }
    single { ApiClient() }
    single { UserDao(get()) }
    single { LocationDao(get()) }
    single { AreaDao(get()) }
    single { FoodDao(get()) }
    single { EventDao(get()) }
    // models
    factory { LocationListModel(get(), get()) }
    factory { LocationCreatorModel(get(), get()) }
    factory { AreaListModel(get()) }
    factory { AreaCreatorModel(get()) }
    factory { UserCreatorModel(get()) }
    factory { EventCreatorModel(get(), get()) }
    factory { EventListModel(get()) }
    factory { FoodModel(get()) }
    factory { HomeModel(get()) }
    factory { LoginModel(get()) }
}