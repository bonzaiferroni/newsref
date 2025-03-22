package newsref.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.io.*
import newsref.app.model.*
import newsref.model.core.ArticleType
import newsref.model.core.editArticleTypeOptions

@Composable
fun ArticlePropertiesColumn(
    article: Article,
    viewModel: ArticlePropertiesModel = viewModel { ArticlePropertiesModel(article) }
) {
    val userState by LocalUserContext.current.state.collectAsState()
    val state by viewModel.state.collectAsState()

    EditArticleTypeFloatyBox(
        editingArticleType = state.editingArticleType,
        articleType = state.articleType,
        canSubmit = state.isValidArticleType,
        commentText = state.comment,
        onChangeComment = viewModel::setComment,
        onDismiss = viewModel::toggleEditingArticleType,
        onSubmit = viewModel::sendArticleTypeEdit,
        selectArticleType = viewModel::selectArticleType
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
fun EditArticleTypeFloatyBox(
    editingArticleType: Boolean,
    articleType: ArticleType,
    canSubmit: Boolean,
    commentText: String,
    onChangeComment: (String) -> Unit,
    onDismiss: () -> Unit,
    selectArticleType: (ArticleType) -> Unit,
    onSubmit: () -> Unit,
) {
    FloatyBox(editingArticleType, onDismiss) {
        Column(
            modifier = Modifier.clip(Blip.ruler.roundBottom)
        ) {
            H2("Edit Article Type")
            RadioGroup(articleType, selectArticleType) {
                editArticleTypeOptions.map {
                    RadioOption(it.value) {
                        Markdown(it.label, mdColors, mdTypography, Modifier)
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