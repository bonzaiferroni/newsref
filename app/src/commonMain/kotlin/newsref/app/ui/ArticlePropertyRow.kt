package newsref.app.ui

import androidx.compose.runtime.*
import newsref.app.blip.controls.*
import newsref.model.data.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.data.Page
import newsref.model.utils.formatSpanLong

@Composable
fun ArticlePropertyRow(
    page: Page,
) {
    PropertyRow {
        PropertyTile("Url", page.url)
        PropertyTile("Type", page.articleType) {
            Text(it.title)
            HuddleEditorControl(
                huddleName = "Edit Article Type",
                key = HuddleKey(
                    pageId = page.id,
                    type = HuddleType.EditArticleType
                ),
            )
        }

        PropertyTile("Word Count", page.wordCount)
        PropertyTile("Score", page.score)
        PropertyTile("Id", page.id)
        page.publishedAt?.let { PropertyTile("Published", it.formatSpanLong()) }
            ?: PropertyTile("Seen", page.seenAt.formatSpanLong())
        page.accessedAt?.let { PropertyTile("Accessed", it.formatSpanLong()) }
            ?: PropertyTile("Accessed", "never")
    }
}