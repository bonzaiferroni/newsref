package newsref.app.ui

import androidx.compose.runtime.Composable
import newsref.app.blip.controls.Tab
import newsref.app.blip.controls.TabCard
import newsref.app.blip.controls.Text
import newsref.app.model.Host
import newsref.model.data.Page
import newsref.model.dto.LogKey

@Composable
fun PageTabs(
    tab: String?,
    onChangeTab: (String) -> Unit,
    page: Page,
    host: Host?,
) {
    TabCard(
        tab,
        onChangeTab,
    ) {
        Tab("Details") { ArticlePropertyRow(page) }
        Tab("Summary", isVisible = page.summary != null) { Text(page.summary!!) }
        Tab("Embed", isVisible = page.embed != null) { Text(page.embed!!) }
        Tab("Source") { HostProperties(host) }
        Tab("Log", scrollable = false) { LogView(LogKey(pageId = page.id))}
    }
}