package streetlight.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import streetlight.app.data.FoodDao
import streetlight.app.data.UserDao
import streetlight.app.ui.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import streetlight.app.data.AreaDao
import streetlight.app.data.LocationDao
import streetlight.app.data.ApiClient
import streetlight.app.data.EventDao
import streetlight.app.ui.area.AreaListModel
import streetlight.app.ui.area.CreateAreaModel
import streetlight.app.ui.event.CreateEventModel
import streetlight.app.ui.location.CreateLocationModel
import streetlight.app.ui.location.LocationListModel
import streetlight.app.ui.login.LoginModel

val di = DI {
    bindProvider {
        PantryDb(
            driver = DatabaseDriverFactory().create()
        )
    }
    // daos
    bindSingleton { ApiClient() }
    bindProvider { UserDao(instance()) }
    bindProvider { LocationDao(instance()) }
    bindProvider { AreaDao(instance()) }
    bindProvider { FoodDao(instance()) }
    bindProvider { EventDao(instance()) }
    // models
    bindProvider { LocationListModel(instance(), instance()) }
    bindProvider { AreaListModel(instance()) }
    bindProvider { CreateUserModel(instance()) }
    bindProvider { CreateLocationModel(instance(), instance()) }
    bindProvider { CreateAreaModel(instance()) }
    bindProvider { CreateEventModel(instance(), instance()) }
    bindProvider { FoodModel(instance()) }
    bindProvider { HomeModel(instance()) }
    bindProvider { LoginModel(instance()) }
    // bindProvider { LocationListModel(instance()) }
}

@Composable
@Preview
fun App() = withDI(di) {
    MaterialTheme {
        // val db = YourDatabaseName.Schema.create(sqlDriver)
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}