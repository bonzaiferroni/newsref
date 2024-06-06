package streetlight.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import streetlight.app.sql.FoodDao
import streetlight.app.io.UserDao
import streetlight.app.ui.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import streetlight.app.io.AreaDao
import streetlight.app.io.LocationDao
import streetlight.app.io.ApiClient
import streetlight.app.io.EventDao
import streetlight.app.sql.FoodModel
import streetlight.app.ui.data.AreaListModel
import streetlight.app.ui.data.AreaCreatorModel
import streetlight.app.ui.data.EventCreatorModel
import streetlight.app.ui.data.EventListModel
import streetlight.app.ui.data.LocationCreatorModel
import streetlight.app.ui.data.LocationListModel
import streetlight.app.ui.data.UserCreatorModel
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
    bindProvider { LocationCreatorModel(instance(), instance()) }
    bindProvider { AreaListModel(instance()) }
    bindProvider { AreaCreatorModel(instance()) }
    bindProvider { UserCreatorModel(instance()) }
    bindProvider { EventCreatorModel(instance(), instance()) }
    bindProvider { EventListModel(instance()) }
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