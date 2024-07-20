package streetlight.app

import org.koin.dsl.module
import streetlight.app.io.ApiClient
import streetlight.app.io.AreaDao
import streetlight.app.io.EventDao
import streetlight.app.io.LocationDao
import streetlight.app.io.SongDao
import streetlight.app.io.RequestDao
import streetlight.app.io.UserDao
import streetlight.app.services.BusService
import streetlight.app.ui.main.EventProfileModel
import streetlight.app.ui.debug.DebugModel
import streetlight.app.ui.debug.AreaEditorModel
import streetlight.app.ui.debug.AreaListModel
import streetlight.app.ui.debug.EventEditorModel
import streetlight.app.ui.debug.EventListModel
import streetlight.app.ui.debug.LocationEditorModel
import streetlight.app.ui.debug.LocationListModel
import streetlight.app.ui.debug.SongEditorModel
import streetlight.app.ui.debug.SongListModel
import streetlight.app.ui.debug.RequestEditorModel
import streetlight.app.ui.debug.RequestListModel
import streetlight.app.ui.debug.UserEditorModel
import streetlight.app.ui.login.LoginModel
import streetlight.app.ui.main.NowModel

val myModule = module {
    // your dependencies here
    single { ApiClient() }
    single { UserDao(get()) }
    single { LocationDao(get()) }
    single { AreaDao(get()) }
    single { EventDao(get()) }
    single { SongDao(get()) }
    single { RequestDao(get()) }
    // supply
    single { BusService() }
    // models
    factory { LocationListModel(get(), get(), get()) }
    factory { (id: Int?) -> LocationEditorModel(id, get(), get(), get()) }
    factory { AreaListModel(get(), get()) }
    factory { (id: Int?) -> AreaEditorModel(id, get(), get()) }
    factory { UserEditorModel(get()) }
    factory { (id: Int?) -> EventEditorModel(id, get(), get(), get()) }
    factory { EventListModel(get(), get()) }
    factory { SongListModel(get(), get()) }
    factory { (id: Int?) -> SongEditorModel(id, get(), get()) }
    factory { (id: Int?) -> RequestEditorModel(id, get(), get(), get(), get()) }
    factory { RequestListModel(get(), get()) }
    factory { DebugModel(get()) }
    factory { LoginModel(get()) }
    factory { (id: Int?) -> EventProfileModel(id!!, get(), get()) }
    factory { NowModel(get(), get()) }
}