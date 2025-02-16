package newsref.app.blip.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

interface BlipTypography {
    val title: TextStyle
    val h1: TextStyle
    val body: TextStyle
}

object DefaultTypography : BlipTypography {
    override val title = base.copy(
        fontSize = 20.sp
    )
    override val h1 = base.copy(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
    override val body = base.copy(
    )
}

private val base = TextStyle.Default