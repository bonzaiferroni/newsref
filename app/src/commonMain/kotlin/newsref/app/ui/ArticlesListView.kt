package newsref.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import newsref.app.blip.controls.ButtonToggle
import newsref.app.blip.core.StateModel
import newsref.app.blip.theme.Blip
import newsref.app.model.SourceBit
import newsref.model.core.ArticleType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SourcesListView(
    articles: ImmutableList<SourceBit>,
    viewModelKey: String = "SourcesListView",
    chapterId: Long? = null,
    viewModel: ArticlesListModel = viewModel(key = viewModelKey) { ArticlesListModel(articles) }
) {
    val state by viewModel.state.collectAsState()
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Blip.ruler.halfSpacing, Alignment.CenterHorizontally),
        verticalArrangement = Blip.ruler.columnGrouped,
        modifier = Modifier.padding(vertical = Blip.ruler.halfSpacing)
            .fillMaxWidth()
    ) {
        if (state.reportCount > 0)
            ButtonToggle(state.showReports, "Reports: ${state.reportCount}", onToggle = { viewModel.toggleType(ArticleType.Report)})
        if (state.perspectiveCount > 0)
            ButtonToggle(state.showPerspectives, "Perspectives: ${state.perspectiveCount}", onToggle = { viewModel.toggleType(ArticleType.Perspective)})
        if (state.analysisCount > 0)
            ButtonToggle(state.showAnalysis, "Analyses: ${state.analysisCount}", onToggle = { viewModel.toggleType(ArticleType.Analysis)})
        if (state.investigationCount > 0)
            ButtonToggle(state.showInvestigations, "Investigations: ${state.investigationCount}", onToggle = { viewModel.toggleType(ArticleType.Investigation)})
        if (state.unknownCount > 0)
            ButtonToggle(state.showUnknown, "Unknown: ${state.unknownCount}", onToggle = { viewModel.toggleType(ArticleType.Unknown)})
    }
    LazyColumn(
    ) {
        items(state.filteredArticles) {
            SourceBitItem(it, chapterId)
        }
    }
}

class ArticlesListModel(
    private val articles: List<SourceBit>
): StateModel<ArticlesListState>(ArticlesListState()) {

    init {
        setState { it.copy(
            reportCount = articles.count { it.articleType == ArticleType.Report },
            analysisCount = articles.count { it.articleType == ArticleType.Analysis },
            perspectiveCount = articles.count { it.articleType == ArticleType.Perspective },
            investigationCount = articles.count { it.articleType == ArticleType.Investigation },
            unknownCount = articles.count { it.articleType == ArticleType.Unknown }
        ) }
        filterArticles()
    }

    fun toggleType(articleType: ArticleType) {
        when (articleType) {
            ArticleType.Report -> setState { it.copy(showReports = !it.showReports) }
            ArticleType.Perspective -> setState { it.copy(showPerspectives = !it.showPerspectives) }
            ArticleType.Analysis -> setState { it.copy(showAnalysis = !it.showAnalysis) }
            ArticleType.Investigation -> setState { it.copy(showInvestigations = !it.showInvestigations) }
            ArticleType.Unknown -> setState { it.copy(showUnknown = !it.showUnknown) }
        }
        filterArticles()
    }

    fun filterArticles() {
        val filteredArticles = articles.filter {
            it.articleType == ArticleType.Report && stateNow.showReports ||
                    it.articleType == ArticleType.Perspective && stateNow.showPerspectives ||
                    it.articleType == ArticleType.Analysis && stateNow.showAnalysis ||
                    it.articleType == ArticleType.Investigation && stateNow.showInvestigations ||
                    it.articleType == ArticleType.Unknown && stateNow.showUnknown
        }
        setState { it.copy(filteredArticles = filteredArticles.toImmutableList()) }
    }
}

data class ArticlesListState(
    val showReports: Boolean = true,
    val showPerspectives: Boolean = true,
    val showAnalysis: Boolean = true,
    val showInvestigations: Boolean = true,
    val showUnknown: Boolean = true,
    val reportCount: Int = 0,
    val perspectiveCount: Int = 0,
    val analysisCount: Int = 0,
    val investigationCount: Int = 0,
    val unknownCount: Int = 0,
    val filteredArticles: ImmutableList<SourceBit> = persistentListOf()
)