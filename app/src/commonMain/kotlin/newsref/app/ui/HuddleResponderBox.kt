package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import newsref.app.blip.controls.Button
import newsref.app.blip.controls.FloatyContent
import newsref.app.blip.controls.H2
import newsref.app.blip.controls.RadioContent
import newsref.app.blip.controls.RadioGroup
import newsref.app.blip.controls.Tab
import newsref.app.blip.controls.TabCard
import newsref.app.blip.controls.Text
import newsref.app.blip.controls.TextField
import newsref.app.blip.theme.Blip
import newsref.model.data.HuddleKey

@Composable
fun HuddleResponderBox(
    huddleName: String,
    showBox: Boolean,
    key: HuddleKey,
    onDismiss: () -> Unit,
    viewModel: HuddleResponderModel = viewModel { HuddleResponderModel(key) }
) {
    val state by viewModel.state.collectAsState()

    FloatyContent(showBox, onDismiss) {
        TabCard(
            currentTab = state.tab,
            onChangePage = viewModel::changeTab,
            shape = Blip.ruler.rounded,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(name = "Guide") {
                Markdown(state.guide, mdColors, mdTypography, Modifier)
            }
            Tab(name = EDIT_TAB_NAME) {
                Column(
                    modifier = Modifier.clip(Blip.ruler.roundBottom)
                ) {
                    H2(huddleName)
                    RadioGroup(state.selectedValue, viewModel::selectValue) {
                        state.options.map { option ->
                            RadioContent(option) {
                                Markdown(option.label, mdColors, mdTypography, Modifier)
                            }
                        }
                    }
                    TextField(
                        text = state.commentText,
                        onTextChange = viewModel::setCommentText,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) { Text("Cancel") }
                        Button(
                            onClick = viewModel::submit,
                            isEnabled = state.canSubmit,
                            modifier = Modifier.weight(1f)
                        ) { Text("Send") }
                    }
                }
            }
            Tab(name = HUDDLE_TAB_NAME, scrollable = false, isVisible = state.activeId != null) {
                ActiveHuddleView(
                    huddleName,
                    state.activeId!!,
                    state.userResponseId
                )
            }
        }
    }
}

