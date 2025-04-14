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
import newsref.app.pond.controls.ButtonToggle
import newsref.app.pond.core.StateModel
import newsref.app.pond.theme.Pond
import newsref.model.data.ArticleType
import newsref.model.data.ChapterPageLite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChapterPagesListView(
    pages: ImmutableList<ChapterPageLite>,
    viewModelKey: String = "ChapterPagesListView",
    chapterId: Long? = null,
    viewModel: ArticlesListModel = viewModel(key = viewModelKey) { ArticlesListModel(pages) }
) {
    val state by viewModel.state.collectAsState()
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Pond.ruler.halfSpacing, Alignment.CenterHorizontally),
        verticalArrangement = Pond.ruler.columnGrouped,
        modifier = Modifier.padding(vertical = Pond.ruler.halfSpacing)
            .fillMaxWidth()
    ) {
        if (state.reportCount > 0)
            ButtonToggle(state.showReports, "Reports: ${state.reportCount}", onToggle = { viewModel.toggleType(ArticleType.Report)})
        if (state.opinionCount > 0)
            ButtonToggle(state.showPerspectives, "Opinions: ${state.opinionCount}", onToggle = { viewModel.toggleType(ArticleType.Opinion)})
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
            ChapterPageLiteItem(
                chapterPage = it,
                chapterId = chapterId,
                modifier = Modifier.animateItem()
            )
        }
    }
}

class ArticlesListModel(
    private val articles: List<ChapterPageLite>
): StateModel<ArticlesListState>(ArticlesListState()) {

    init {

        setState { it.copy(
            reportCount = articles.count { it.page.articleType == ArticleType.Report },
            analysisCount = articles.count { it.page.articleType == ArticleType.Analysis },
            opinionCount = articles.count { it.page.articleType == ArticleType.Opinion },
            investigationCount = articles.count { it.page.articleType == ArticleType.Investigation },
            unknownCount = articles.count { it.page.articleType == ArticleType.Unknown }
        ) }
        filterArticles()
    }

    fun toggleType(articleType: ArticleType) {
        when (articleType) {
            ArticleType.Report -> setState { it.copy(showReports = !it.showReports) }
            ArticleType.Opinion -> setState { it.copy(showPerspectives = !it.showPerspectives) }
            ArticleType.Analysis -> setState { it.copy(showAnalysis = !it.showAnalysis) }
            ArticleType.Investigation -> setState { it.copy(showInvestigations = !it.showInvestigations) }
            ArticleType.Unknown -> setState { it.copy(showUnknown = !it.showUnknown) }
        }
        filterArticles()
    }

    fun filterArticles() {
        val filteredArticles = articles.filter {
            it.page.articleType == ArticleType.Report && stateNow.showReports ||
                    it.page.articleType == ArticleType.Opinion && stateNow.showPerspectives ||
                    it.page.articleType == ArticleType.Analysis && stateNow.showAnalysis ||
                    it.page.articleType == ArticleType.Investigation && stateNow.showInvestigations ||
                    it.page.articleType == ArticleType.Unknown && stateNow.showUnknown
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
    val opinionCount: Int = 0,
    val analysisCount: Int = 0,
    val investigationCount: Int = 0,
    val unknownCount: Int = 0,
    val filteredArticles: ImmutableList<ChapterPageLite> = persistentListOf()
)