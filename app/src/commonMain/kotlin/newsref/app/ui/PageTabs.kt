package newsref.app.ui

import androidx.compose.runtime.Composable
import pondui.ui.controls.Tab
import pondui.ui.controls.TabCard
import pondui.ui.controls.Text
import newsref.model.data.ChapterPage
import newsref.model.data.Host
import newsref.model.data.Page
import newsref.model.data.LogKey

@Composable
fun PageTabs(
    tab: String?,
    onChangeTab: (String) -> Unit,
    page: Page,
    host: Host?,
    chapterPage: ChapterPage? = null
) {
    TabCard() {
        Tab("Details") { PagePropertyRow(page, chapterPage) }
        Tab("Summary", isVisible = page.summary != null) { Text(page.summary!!) }
        Tab("Embed", isVisible = page.embed != null) { Text(page.embed!!) }
        Tab("Source") { HostProperties(host) }
        Tab("Log", scrollable = false) { LogView(LogKey(pageId = page.id))}
    }
}