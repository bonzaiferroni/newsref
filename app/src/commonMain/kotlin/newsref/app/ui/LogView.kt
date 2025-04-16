package newsref.app.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import pondui.ui.controls.Text
import newsref.model.data.LogKey
import newsref.model.utils.formatSpanBrief

@Composable
fun LogView(
    key: LogKey,
    viewModel: LogModel = viewModel { LogModel(key) }
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.logs) { log ->
            val annotatedMessage = remember(log.id) {
                if (log.message.contains("http")) {
                    messageToAnnotatedString(log.message)
                } else {
                    null
                }
            }

            SelectionContainer {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = log.subject,
                        modifier = Modifier.weight(.2f),
                        maxLines = 1
                    )
                    if (annotatedMessage != null) {
                        Text(
                            text = annotatedMessage,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = log.message,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = log.origin,
                        modifier = Modifier.weight(.2f),
                        maxLines = 1
                    )
                    Text(
                        text = (Clock.System.now() - log.time).formatSpanBrief(),
                        modifier = Modifier.weight(.1f),
                        maxLines = 1,
                        style = TextStyle(textAlign = TextAlign.Right)
                    )
                }
            }
        }
    }
}

private fun messageToAnnotatedString(message: String) = message.split('\n').let { split ->
    buildAnnotatedString {
        split.forEachIndexed { index, part ->
            if (part.startsWith("http") && !part.contains(' ')) {
                withLink(LinkAnnotation.Url(url = part, styles = TextLinkStyles(style = SpanStyle(color = Color.Blue)))) {
                    append(part)
                }
            } else {
                append(part)
            }
            if (index + 1 < split.size) {
                appendLine()
            }
        }
    }
}