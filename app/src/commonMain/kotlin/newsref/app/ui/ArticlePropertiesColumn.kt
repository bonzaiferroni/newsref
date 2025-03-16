package newsref.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.blip.controls.*
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
        onDismiss = viewModel::toggleEditingArticleType,
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
    onDismiss: () -> Unit,
    selectArticleType: (ArticleType) -> Unit
) {
    val options = remember { editArticleTypeOptions.map { it.value to it.label }}

    FloatyBox(editingArticleType, onDismiss) {
        Column {
            H2("Edit Article Type")
            RadioGroup(options, articleType, selectArticleType)
        }
    }
}

@Composable
fun <T> RadioGroup(
    options: List<Pair<T, String>>,
    selectedValue: T,
    onOptionSelected: (T) -> Unit
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { (value, label) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = value == selectedValue,
                        onClick = { onOptionSelected(value) }
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(
                    Modifier
                        .size(24.dp)
                        .padding(4.dp)
                ) {
                    drawCircle(
                        color = if (value == selectedValue) Color.Blue else Color.Gray,
                        style = if (value == selectedValue) Fill else Stroke(2.dp.toPx())
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(label)
            }
        }
    }
}