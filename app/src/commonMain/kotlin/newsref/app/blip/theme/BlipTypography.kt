package newsref.app.blip.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

interface BlipTypography {
    val title: TextStyle
    val body: TextStyle
}

object DefaultTypography : BlipTypography {
    override val title: TextStyle = TextStyle.Default.copy(
        fontSize = 20.sp
    )
    override val body: TextStyle = TextStyle.Default.copy(
    )
}