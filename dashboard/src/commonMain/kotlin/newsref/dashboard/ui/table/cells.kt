package newsref.dashboard.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
    onClickCell: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .background(color = color)
            .border(2.dp, color.darken(.05f))
            .padding(4.dp)
            .apply { onClickCell?.let { this.clickable(onClick = it) } }
    ) {
        content()
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
    val count = counts[id] ?: 0
    Row {
        Text(text = "$count")
        for (addition in additions) {
            val added = addition[id]
            if (added == count || added == 0) continue
            Spacer(modifier = Modifier.width(8.dp))
            val additionText = addition[id]?.let { "+${it}" } ?: ""
            Text(text = additionText, modifier = Modifier.widthIn(min = 30.dp))
        }
    }
}