package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.utils.formatSpanLong

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticlePropertiesColumn(
    article: Article,
    viewModel: ArticlePropertiesModel = viewModel { ArticlePropertiesModel(article) }
) {
    val userState by LocalUserContext.current.state.collectAsState()
    val state by viewModel.state.collectAsState()

    Column {
        H2(article.bestTitle)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Blip.ruler.rowSpaced,
            verticalArrangement = Blip.ruler.columnSpaced,
        ) {
            PropertyTile("url") {
                Text(article.url)
            }
            article.articleType?.let {
                PropertyTile("Type") {
                    Text(it.title)
                    if (userState.isLoggedIn) {
                        HuddleEditorControl(
                            huddleName = "Edit Article Type",
                            key = HuddleKey(
                                pageId = state.article.pageId,
                                type = HuddleType.EditArticleType
                            ),
                        )
                    }
                }
            }

            article.wordCount?.let { PropertyTile("Word Count", it) }
            article.score?.let { PropertyTile("Score", it) }
            article.publishedAt?.let { PropertyTile("Published", it.formatSpanLong()) }
                ?: PropertyTile("Seen", article.seenAt.formatSpanLong())
            article.accessedAt?.let { PropertyTile("Accessed", it.formatSpanLong()) }
                ?: PropertyTile("Accessed", "never")
        }
    }
}