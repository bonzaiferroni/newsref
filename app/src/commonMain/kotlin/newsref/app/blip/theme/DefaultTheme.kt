package newsref.app.blip.theme

import androidx.compose.ui.graphics.Color

object DefaultTheme : FuiTheme {
    override val layout: FuiLayout = object : FuiLayout{
        override val spacing: Int = 16
    }

    override val colors: FuiColors = object : FuiColors{
        override val primary: Color = Color(0xFFC0C0C0)
    }
}