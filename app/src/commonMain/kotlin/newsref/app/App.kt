package newsref.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import newsref.app.fui.ProvideTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import newsref.app.generated.resources.Res
import newsref.app.generated.resources.compose_multiplatform
import newsref.app.nav.AppNavigator

@Composable
@Preview
fun App() {
    ProvideTheme{
        AppNavigator(StartRoute)
    }
}