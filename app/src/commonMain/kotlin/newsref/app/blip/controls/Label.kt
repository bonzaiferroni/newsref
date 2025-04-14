package newsref.app.blip.controls

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import newsref.app.blip.theme.Blip

@Composable
fun PropertyLabel(
    propertyName: String,
    value: Any,
    style: TextStyle = Blip.typo.label,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    val propertyColor = Blip.localColors.contentDim
    val valueColor = Blip.colors.shine
    BasicText(
        text = remember { buildAnnotatedString {
            withStyle(style = SpanStyle(color = propertyColor)) {
                append("$propertyName: ")
            }
            withStyle(style = SpanStyle(color = valueColor)) {
                append(value.toString())
            }
        } },
        modifier = modifier,
        style = style,
        maxLines = maxLines,
    )
}