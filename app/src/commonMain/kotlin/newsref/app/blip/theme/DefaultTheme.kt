package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

object DefaultTheme : BlipTheme {
    override val layout: BlipLayout = object : BlipLayout{
        override val spacing: Int = 16
    }

    override val colors: BlipColors = object : BlipColors{
        override val primary: Color = Color(0xFFc0c0c0)
        override val content: Color = Color.White
        override val background: Color = Color(0xFFa5c097)
        override val surface: Color = Color(0xFFdcdcdc)
    }
}