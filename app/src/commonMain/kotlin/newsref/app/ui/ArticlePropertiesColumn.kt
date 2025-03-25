package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.*
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.utils.formatSpanLong

@Composable
fun ArticlePropertiesColumn(
    article: Article,
    viewModel: ArticlePropertiesModel = viewModel { ArticlePropertiesModel(article) }
) {
    val userState by LocalUserContext.current.state.collectAsState()
    val state by viewModel.state.collectAsState()

    HuddleResponderBox(
        huddleName = "Edit Article Type",
        showBox = state.editingArticleType,
        key = HuddleKey(
            pageId = state.article.pageId,
            type = HuddleType.EditArticleType
        ),
        onDismiss = viewModel::toggleEditingArticleType
    )

    Column {
        H2(article.bestTitle)
        Text(article.url)
        article.articleType?.let {
            val label = "Type: ${it.title}"
            if (userState.isLoggedIn) {
                Button(viewModel::toggleEditingArticleType) { Text(label) }
            } else {
                Text(label)
            }
        }

        article.wordCount?.let { Text("Word count: $it") }
        article.score?.let { Text("Score: $it") }
        article.publishedAt?.let { Text("Published: ${it.formatSpanLong()}") }
            ?: Text("Seen: ${article.seenAt.formatSpanLong()}")
        article.accessedAt?.let { Text("Accessed: ${it.formatSpanLong()}") }
            ?: Text("Accessed: never")
    }
}