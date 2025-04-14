package newsref.app.ui

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.app.*
import newsref.app.pond.controls.*
import newsref.app.pond.nav.Scaffold
import newsref.app.pond.theme.Pond
import newsref.model.data.Host

@Composable
fun ChapterPageScreen(
    route: ChapterPageRoute,
    viewModel: ChapterPageModel = viewModel { ChapterPageModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val chapter = state.chapterPack
    if (chapter == null) return

    Scaffold(
        verticalArrangement = Pond.ruler.columnTight
    ) {
        BalloonChart(
            selectedId = state.selectedId,
            balloons = state.balloons,
            height = 200.dp,
            onClickBalloon = viewModel::selectPage
        )
        val chapterSource = state.chapterPage
        val article = state.page
        val host = state.host
        if (chapterSource == null || article == null || host == null) return@Scaffold

        val height = 130f
        ChapterPageHeader(
            height = height,
            page = article,
            chapterPage = chapterSource,
            host = host,
            chapter = chapter
        )
        PageTabs(
            tab = state.tab,
            onChangeTab = viewModel::onChangeTab,
            page = article,
            host = host,
            chapterPage = state.chapterPage
        )
    }
}

@Composable
fun HostProperties(
    host: Host?
) {
    if (host == null) return
    H2(host.name ?: host.core)
    Text("Domain: ${host.core}")
    Text("Score: ${host.score}")
}