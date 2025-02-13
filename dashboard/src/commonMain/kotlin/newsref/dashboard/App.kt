package newsref.dashboard

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.runBlocking
import newsref.dashboard.generated.resources.Res
import newsref.dashboard.nav.Navigator

import newsref.dashboard.ui.theme.AppTheme
import newsref.dashboard.utils.ToolTipper
import newsref.db.initDb
import newsref.db.readEnvFromText
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
@Preview
fun App(
    initialRoute: ScreenRoute,
    changeRoute: (ScreenRoute) -> Unit,
    exitApp: () -> Unit,
) {
    val env = remember {
        runBlocking {
            val envContent = readText("files/.env")
            val env = readEnvFromText(envContent)
            println(env.read("HF_KEY"))
            initDb(env)
            env
        }
    }

    val context = AppContext(exitApp)
    AppTheme(true) {
        ToolTipper {
            Navigator(initialRoute, context, changeRoute)
        }
    }
}

class AppContext(
    val exitApp: () -> Unit
)

@OptIn(ExperimentalResourceApi::class)
suspend fun readBytes(path: String) = Res.readBytes(path)

suspend fun readText(path: String) = readBytes(path).toString(Charsets.UTF_8)