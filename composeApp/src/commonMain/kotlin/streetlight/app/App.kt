package streetlight.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import streetlight.app.ui.theme.AppTheme

@Composable
fun App() {
    startKoin {
        modules(myModule)
    }
    PreComposeApp {
        KoinContext {
            AppTheme(
            ) {
                Surface {
                    val navigator = rememberNavigator()
                    NavHost(
                        // Assign the navigator to the NavHost
                        navigator = navigator,
                        // Navigation transition for the scenes in this NavHost, this is optional
                        navTransition = NavTransition(
                            createTransition = fadeIn() + scaleIn(initialScale = 0.9f),
                            destroyTransition = fadeOut() + scaleOut(targetScale = 0.9f),
                            pauseTransition = fadeOut() + scaleOut(targetScale = 1.1f),
                            resumeTransition = fadeIn() + scaleIn(initialScale = 1.1f),
                            enterTargetContentZIndex = 0f,
                            exitTargetContentZIndex = 0f
                        ),
                        // The start destination
                        initialRoute = Scenes.default(),
                    ) {
                        appScenes(navigator)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    App()
}
