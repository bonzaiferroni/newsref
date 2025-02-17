package newsref.app.blip.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import newsref.app.generated.resources.PlayfairDisplay_Regular
import newsref.app.generated.resources.Res
import newsref.app.generated.resources.oranienbaum_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi

interface BlipTypography {
    val title: TextStyle
    val h1: TextStyle
    val body: TextStyle
}

@Composable
fun DefaultTypography() = object : BlipTypography {
    override val title = base.copy(
        fontSize = 20.sp,
    )
    override val h1 = base.copy(
        fontSize = 24.sp,
        fontSynthesis = FontSynthesis.None,
        fontWeight = FontWeight.Normal,
        fontFamily = PlayfairFontFamily()
    )
    override val body = base
}

private val base = TextStyle.Default


@Composable
fun PlayfairFontFamily() = FontFamily(
    Font(Res.font.oranienbaum_regular, weight = FontWeight.Normal),
)