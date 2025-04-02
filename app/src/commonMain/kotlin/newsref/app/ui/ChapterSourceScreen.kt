package newsref.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.blip.controls.*
import newsref.app.blip.theme.Blip
import newsref.app.model.*

@Composable
fun ChapterPageScreen(
    route: ChapterPageRoute,
    viewModel: ChapterPageModel = viewModel { ChapterPageModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val chapter = state.chapter
    if (chapter == null) return

    Column(
        verticalArrangement = Blip.ruler.columnTight
    ) {
        BalloonChart(
            selectedId = state.selectedId,
            balloons = state.balloons,
            height = 400.dp,
            onClickBalloon = viewModel::selectSource
        )
        val chapterSource = state.chapterPage
        val article = state.article
        val host = state.host
        if (chapterSource == null || article == null || host == null) return@Column

        val height = 130f
        ChapterPageHeader(
            height = height,
            article = article,
            chapterPage = chapterSource,
            host = host,
            chapter = chapter
        )
        TabCard(
            state.page,
            viewModel::onChangePage,
        ) {
            Tab("Details") {
                ArticlePropertyRow(article)
            }
            Tab("Summary", isVisible = article.summary != null) { Text(article.summary!!) }
            Tab("Embed", isVisible = article.embed != null) { Text(article.embed!!) }
            Tab("Publisher") { HostProperties(host) }
        }
    }
}

@Composable
fun HostProperties(
    host: Host
) {
    H2(host.name ?: host.core)
    Text("Domain: ${host.core}")
    Text("Score: ${host.score}")
}