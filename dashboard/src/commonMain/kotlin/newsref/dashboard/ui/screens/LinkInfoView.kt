package newsref.dashboard.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import newsref.dashboard.emptyEmoji
import newsref.dashboard.ui.table.ColumnGroup
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.addControl
import newsref.dashboard.ui.table.openExternalLink
import newsref.model.dto.LinkInfo

@Composable
fun LinkInfoView(
    name: String,
    links: List<LinkInfo>,
) {
    val uriHandler = LocalUriHandler.current

    DataTable(
        name = name,
        rows = links,
        columns = listOf(
            ColumnGroup(
                TableColumn<LinkInfo>("Headline", weight = 1f) { TextCell(it.headline) }
                    .addControl(openExternalLink(uriHandler) { it.url })
            ),
            ColumnGroup(
                TableColumn<LinkInfo>("Origin", width = 60) { TextCell(it.originId) },
                TableColumn<LinkInfo>("Origin Url", weight = 1f) { TextCell(it.originUrl) },
            ),
            ColumnGroup(
                TableColumn<LinkInfo>("Target", width = 60) { TextCell(it.targetId) },
                TableColumn<LinkInfo>("Target Url", weight = 1f) { TextCell(it.url) },
            ),
            ColumnGroup(
                TableColumn<LinkInfo>("textIndex") { TextCell(it.textIndex) }
            ),
            ColumnGroup(
                TableColumn<LinkInfo>("Text", weight = 1f) { LinkSnippet(it) }
            )
        )
    )
}

@Composable
fun LinkSnippet(info: LinkInfo) {
    val context = info.context
    if (context == null) {
        Text(emptyEmoji)
        return
    }
    if (info.textIndex < 0) {
        Text(context)
        return
    }

    val annotatedText = buildAnnotatedString {
        append(context.substring(0, info.textIndex))

        // Add a clickable link style
        pushStringAnnotation(tag = "URL", annotation = info.url)
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
            append(info.urlText)
        }
        pop()
        append(context.substring(info.textIndex + info.urlText.length))
    }

    Text(
        text = annotatedText,
        onTextLayout = { layoutResult ->
            // Optional: Handle clicks specifically based on spans if needed
        }
    )
}
