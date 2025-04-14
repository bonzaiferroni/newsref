package newsref.app.pond.controls

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import newsref.app.pond.theme.Pond

@Composable
fun PropertyLabel(
    propertyName: String,
    value: Any?,
    style: TextStyle = Pond.typo.label,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    if (value == null) return
    val propertyColor = Pond.localColors.contentDim
    val valueColor = Pond.localColors.highlight
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