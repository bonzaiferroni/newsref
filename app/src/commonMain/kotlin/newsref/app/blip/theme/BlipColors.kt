package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

interface BlipColors {
    val primary: Color
    val contentDark: Color
    val surfaceDark: Color
    val contentLight: Color
    val surfaceLight: Color
    val background: Color
}

enum class ColorMode {
    Light,
    Dark,
}

data class ModeColors(
    val mode: ColorMode,
    val primary: Color,
    val content: Color,
    val surface: Color,
    val background: Color,
)