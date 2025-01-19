package newsref.dashboard.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.dashboard.halfSpacing
import newsref.model.dto.SourceInfo

@Composable
fun SourceContentView(
    source: SourceInfo,
    viewModel: SourceContentModel = viewModel { SourceContentModel(source) }
) {
    val state by viewModel.state.collectAsState()
    Column(
        verticalArrangement = Arrangement.spacedBy(halfSpacing)
    ) {
        state.source.image?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
            )
        }
        for (content in state.contents) {
            SelectionContainer {
                Text(content.text)
            }
        }
    }
}