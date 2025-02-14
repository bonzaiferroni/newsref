package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

object DefaultTheme : BlipTheme {
    override val layout: BlipRuler = object : BlipRuler{
        override val spacing: Int = 16
        override val corner: Int = 12
    }

    override val colors: BlipColors = object : BlipColors{
        override val primary: Color = Color(0xFF57965c)
        override val content: Color = Color.White
        override val background: Color = Color(0xFF1b7161)
        override val surface: Color = Color(0xFFdcdcdc)
    }
}