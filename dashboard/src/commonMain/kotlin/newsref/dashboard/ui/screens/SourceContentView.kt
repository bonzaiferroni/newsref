package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
        for (content in state.contents) {
            Text(content.text)
        }
    }
}