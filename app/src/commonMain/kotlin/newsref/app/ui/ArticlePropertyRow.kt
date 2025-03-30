package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.utils.formatSpanLong

@Composable
fun ArticlePropertyRow(
    article: Article,
) {
    PropertyRow {
        PropertyTile("Url", article.url)
        PropertyTile("Type", article.articleType) {
            Text(it.title)
            HuddleEditorControl(
                huddleName = "Edit Article Type",
                key = HuddleKey(
                    pageId = article.pageId,
                    type = HuddleType.EditArticleType
                ),
            )
        }

        PropertyTile("Word Count", article.wordCount)
        PropertyTile("Score", article.score)
        article.publishedAt?.let { PropertyTile("Published", it.formatSpanLong()) }
            ?: PropertyTile("Seen", article.seenAt.formatSpanLong())
        article.accessedAt?.let { PropertyTile("Accessed", it.formatSpanLong()) }
            ?: PropertyTile("Accessed", "never")
    }
}