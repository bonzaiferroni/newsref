package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey

@Composable
fun ArticlePropertiesColumn(
    article: Article,
    viewModel: ArticlePropertiesModel = viewModel { ArticlePropertiesModel(article) }
) {
    val userState by LocalUserContext.current.state.collectAsState()
    val state by viewModel.state.collectAsState()

    HuddleFloaty(
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
        article.publishedAt?.let { Text("Published: ${it.agoLongFormat()} ago") }
            ?: Text("Seen: ${article.seenAt.agoLongFormat()} ago")
        article.accessedAt?.let { Text("Accessed: ${it.agoLongFormat()} ago") }
            ?: Text("Accessed: never")
    }
}

@Composable
fun <T> HuddleFloatyBox(
    showHuddle: Boolean,
    huddleName: String,
    selected: T,
    options: ImmutableList<RadioOption<T>>,
    canSubmit: Boolean,
    commentText: String,
    onChangeComment: (String) -> Unit,
    onDismiss: () -> Unit,
    selectArticleType: (T) -> Unit,
    onSubmit: () -> Unit,
) {
    FloatyBox(showHuddle, onDismiss) {
        Column(
            modifier = Modifier.clip(Blip.ruler.roundBottom)
        ) {
            H2(huddleName)
            RadioGroup(selected, selectArticleType) {
                options.map { option ->
                    RadioContent(option) {
                        Markdown(option.label, mdColors, mdTypography, Modifier)
                    }
                }.toImmutableList()
            }
            TextField(
                text = commentText,
                onTextChange = onChangeComment,
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }
                Button(
                    onClick = onSubmit,
                    isEnabled = canSubmit,
                    modifier = Modifier.weight(1f)
                ) { Text("Send") }
            }
        }
    }
}