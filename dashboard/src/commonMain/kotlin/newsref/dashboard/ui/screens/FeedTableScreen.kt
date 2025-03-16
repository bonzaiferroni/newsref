package newsref.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import newsref.dashboard.ui.table.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import newsref.app.blip.controls.TabPage
import newsref.app.blip.controls.TabPages
import newsref.app.blip.controls.rememberPages
import newsref.dashboard.FeedItemRoute
import newsref.dashboard.FeedTableRoute
import newsref.dashboard.LocalNavigator
import newsref.model.core.*
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedTableScreen(
    route: FeedTableRoute,
    viewModel: FeedTableModel = viewModel { FeedTableModel(route) }
) {
    val state by viewModel.state.collectAsState()
    val nav = LocalNavigator.current
    LaunchedEffect(state.page) {
        nav.setRoute(route.copy(page = state.page))
    }

    TabPages(
        currentPageName = state.page,
        onChangePage = viewModel::changePage,
        pageContents = rememberPages(
            TabPage(name = "Feeds", scrollbar = false) {
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val switchArgs = listOf(
                        SwitchArg("Disabled", state.showDisabled, viewModel::changeShowDisabled),
                        SwitchArg("External", state.showExternal, viewModel::changeShowExternal),
                        SwitchArg("Track Pos", state.showTrackPosition, viewModel::changeShowTrackPosition),
                        SwitchArg("Selector", state.showSelector, viewModel::changeShowSelector),
                        SwitchArg("Debug", state.showDebug, viewModel::changeShowDebug),
                    )
                    for (switchArg in switchArgs) {
                        Column(
                            modifier = Modifier.width(100.dp)
                                .alpha(.5f)
                        ) {
                            Text(switchArg.name)
                            Switch(switchArg.isOn, switchArg.onSwitch)
                        }
                    }
                }
                DataTable(
                    name = "Feed Table",
                    items = state.items,
                    onClickRow = { nav.go(FeedItemRoute(it.id)) },
                    onSorting = viewModel::changeSorting,
                    glowFunction = { glowOverHour(Clock.System.now() - (Clock.System.now() - it.checkAt) - 1.hours) },
                    columnGroups = listOf(
                        columns(
                            TableColumn(
                                name = "Core", weight = 1f, sort = DataSort.Name,
                            ) { TextCell(it.url.core) },
                            TableColumn(
                                name = "Dis", width = 40, isVisible = state.showDisabled
                            ) { BooleanCell(it.disabled) },
                            TableColumn(
                                name = "Ext", width = 40, isVisible = state.showExternal
                            ) { BooleanCell(it.external) },
                            TableColumn(
                                name = "Trk", width = 40, isVisible = state.showTrackPosition
                            ) { BooleanCell(it.trackPosition) },
                            TableColumn(
                                name = "Slc", width = 40, isVisible = state.showSelector
                            ) { BooleanCell(!it.selector.isNullOrEmpty()) },
                            TableColumn(
                                name = "Db", width = 40, isVisible = state.showDebug
                            ) { BooleanCell(it.debug) },
                            TableColumn(
                                name = "Links", width = 50, align = AlignCell.Right
                            ) { TextCell(it.linkCount) },
                            TableColumn(
                                name = "Total", width = 50, align = AlignCell.Right, sort = DataSort.Score
                            ) { TextCell(state.leadCounts[it.id]) },
                            TableColumn(
                                name = "Check", width = 60, align = AlignCell.Right, sort = DataSort.Time
                            ) { DurationUntilCell(it.checkAt) }
                        )
                    )
                )
            },
            TabPage(name = "New Feed") {
                FeedRowProperties(
                    name = "New Feed",
                    item = state.newItem,
                    href = state.newHref,
                    changeHref = viewModel::changeHref,
                    changeUpdatedItem = viewModel::changeNewItem
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = viewModel::addNewItem, enabled = state.canAddItem) {
                        Text("Add")
                    }
                }
            }
        )
    )


}