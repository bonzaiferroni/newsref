package streetlight.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin


@Composable
@Preview
fun App() {
    startKoin {
        modules(myModule)
    }
    PreComposeApp {
        KoinContext {
            MaterialTheme {
                val navigator = rememberNavigator()
                NavHost(
                    // Assign the navigator to the NavHost
                    navigator = navigator,
                    // Navigation transition for the scenes in this NavHost, this is optional
                    navTransition = NavTransition(),
                    // The start destination
                    initialRoute = Scenes.default(),
                ) {
                    appScenes(navigator)
                }
            }
        }
    }
}