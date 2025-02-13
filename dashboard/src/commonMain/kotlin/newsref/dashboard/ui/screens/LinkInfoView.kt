package newsref.dashboard.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import newsref.dashboard.LocalNavigator
import newsref.dashboard.SourceItemRoute
import newsref.dashboard.emptyEmoji
import newsref.dashboard.ui.table.DataTable
import newsref.dashboard.ui.table.TableColumn
import newsref.dashboard.ui.table.TextCell
import newsref.dashboard.ui.table.columns
import newsref.dashboard.ui.table.onClick
import newsref.dashboard.ui.table.openExternalLink
import newsref.model.dto.LinkInfo

@Composable
fun LinkInfoView(
    name: String,
    links: List<LinkInfo>,
) {
    val uriHandler = LocalUriHandler.current
    val nav = LocalNavigator.current

    DataTable(
        name = name,
        items = links,
        columnGroups = listOf(
            columns(
                TableColumn<LinkInfo>("Ext", width = 30) { TextCell(if (it.isExternal) "⤴" else "🔃")},
                TableColumn<LinkInfo>(
                    name = "Headline", weight = 1f,
                    controls = listOf( openExternalLink { it.url } )
                ) { TextCell(it.headline) }
            ),
            columns(
                TableColumn<LinkInfo>("Origin", width = 60) { TextCell(it.originId) }
                    .onClick { nav.go(SourceItemRoute(it.originId, "Content")) },
                TableColumn<LinkInfo>("Origin Url", weight = 1f) { TextCell(it.originUrl) },
            ),
            columns(
                TableColumn<LinkInfo>("Target", width = 60) { TextCell(it.targetId) }
                    .onClick { nav.go(SourceItemRoute(it.originId, "Content")) },
                TableColumn<LinkInfo>("Target Url", weight = 1f) { TextCell(it.url) },
            ),
            columns(
                TableColumn<LinkInfo>("textIndex") { TextCell(it.textIndex) }
            ),
            columns(
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
