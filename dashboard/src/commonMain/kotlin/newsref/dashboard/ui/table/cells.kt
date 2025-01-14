package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TableCell(
    width: Int,
    color: Color = Color(0f, 0f, 0f, 0f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .background(color = color)
    ) {
        Box(modifier = Modifier.padding(4.dp)) {
            content()
        }
    }
}

@Composable
fun TextCell(
    text: String
) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun CountCell(
    id: Int,
    counts: Map<Int, Int>,
    additions: List<Map<Int, Int?>>
) {
    Row {
        Text(text = "${counts[id]}")
        for (addition in additions) {
            Spacer(modifier = Modifier.width(8.dp))
            val additionText = addition[id]?.let { "+${it}" } ?: ""
            Text(text = additionText, modifier = Modifier.widthIn(min = 30.dp))
        }
    }
}