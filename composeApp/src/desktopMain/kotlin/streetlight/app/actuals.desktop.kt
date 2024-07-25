package streetlight.app

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font

val monoFont = Font("font/RobotoMono_Regular.ttf")

actual val monoFamily: FontFamily
    get() =FontFamily(monoFont)