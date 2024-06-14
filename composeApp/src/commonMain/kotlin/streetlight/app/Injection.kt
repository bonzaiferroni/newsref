package streetlight.app

import org.koin.dsl.module
import streetlight.app.io.ApiClient
import streetlight.app.io.AreaDao
import streetlight.app.io.EventDao
import streetlight.app.io.LocationDao
import streetlight.app.io.PerformanceDao
import streetlight.app.io.RequestDao
import streetlight.app.io.UserDao
import streetlight.app.services.BusService
import streetlight.app.sql.FoodDao
import streetlight.app.sql.FoodModel
import streetlight.app.ui.HomeModel
import streetlight.app.ui.data.AreaEditorModel
import streetlight.app.ui.data.AreaListModel
import streetlight.app.ui.data.EventEditorModel
import streetlight.app.ui.data.EventListModel
import streetlight.app.ui.data.LocationEditorModel
import streetlight.app.ui.data.LocationListModel
import streetlight.app.ui.data.PerformanceEditorModel
import streetlight.app.ui.data.PerformanceListModel
import streetlight.app.ui.data.RequestEditorModel
import streetlight.app.ui.data.RequestEditorScreen
import streetlight.app.ui.data.RequestListModel
import streetlight.app.ui.data.UserEditorModel
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
    single { PerformanceDao(get()) }
    single { RequestDao(get()) }
    // supply
    single { BusService() }
    single { BusService() }
    // models
    factory { LocationListModel(get(), get(), get()) }
    factory { (id: Int?) -> LocationEditorModel(id, get(), get(), get()) }
    factory { AreaListModel(get(), get()) }
    factory { (id: Int?) -> AreaEditorModel(id, get(), get()) }
    factory { UserEditorModel(get()) }
    factory { (id: Int?) -> EventEditorModel(id, get(), get(), get()) }
    factory { EventListModel(get(), get()) }
    factory { PerformanceListModel(get(), get()) }
    factory { (id: Int?) -> PerformanceEditorModel(id, get(), get()) }
    factory { (id: Int?) -> RequestEditorModel(id, get(), get(), get(), get()) }
    factory { RequestListModel(get(), get()) }
    factory { FoodModel(get()) }
    factory { HomeModel(get()) }
    factory { LoginModel(get()) }
}