package newsref.dashboard.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import newsref.dashboard.LocalNavigator
import newsref.dashboard.PageItemRoute
import newsref.dashboard.baseSpacing
import newsref.dashboard.halfSpacing
import newsref.dashboard.ui.controls.ScrollBox
import newsref.dashboard.utils.SpeechPlayer
import newsref.db.model.Content
import newsref.db.model.Page
import newsref.model.data.PageInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageContentView(
    page: Page,
    contents: List<Content>,
    route: PageItemRoute,
    viewModel: PageContentModel = viewModel { PageContentModel(page, contents) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScrollBox(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(halfSpacing)
            ) {
                Spacer(modifier = Modifier.height(baseSpacing))
                page.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                    )
                }

                for (content in contents) {
                    val bringIntoViewRequester = remember { BringIntoViewRequester() }

                    LaunchedEffect(state.playingText) {
                        if (state.playingText == content.text) {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }

                    SelectionContainer(
                        modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
                    ) {
                        val background = when {
                            state.playingText == content.text -> MaterialTheme.colorScheme.secondaryContainer
                            else -> Color.Transparent
                        }
                        Text(
                            text = content.text,
                            modifier = Modifier.background(background)
                        )
                    }
                }
            }
        }
        SpeechPlayer(
            contents = state.contents,
            autoPlay = route.nextSpeakContent != null,
            onPlayText = viewModel::onPlayText,
            onFinished = route.nextSpeakContent?.createSpeakRoute()?.let {
                { nav.go(it) }
            }
        )
    }
}

fun List<Long>.createSpeakRoute(): PageItemRoute? {
    if (this.isEmpty()) return null
    val next = this.first()
    return PageItemRoute(pageId = next, pageName = "Content", nextSpeakContent = this - next)
}

fun List<PageInfo>.toSpeakRoute() = this.map { it.pageId }.createSpeakRoute()