package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import compose.icons.TablerIcons
import compose.icons.tablericons.Edit
import pondui.ui.controls.*
import pondui.ui.theme.Pond
import pondui.io.LocalUserContext
import newsref.model.data.HuddleKey
import pondui.io.collectState

@Composable
fun HuddleEditorControl(
    huddleName: String,
    key: HuddleKey,
    viewModel: HuddleEditorModel = viewModel { HuddleEditorModel(key) }
) {
    val state by viewModel.state.collectAsState()
    val userState by LocalUserContext.collectState()

    IconButton(TablerIcons.Edit) { viewModel.toggleIsOpen() }

    FloatyContent(state.isOpen, viewModel::toggleIsOpen) {
        TabCard(
            shape = Pond.ruler.rounded,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(name = "Guide") {
                SelectionContainer {
                    Markdown(state.guide, mdColors, MdTypography(), Modifier)
                }
            }
            Tab(name = EDIT_TAB_NAME, isVisible = userState.isLoggedIn) {
                Column(
                    modifier = Modifier.clip(Pond.ruler.roundBottom)
                ) {
                    H2(huddleName)
                    RadioGroup(state.selectedValue, viewModel::selectValue) {
                        val options = state.options.map { option ->
                            RadioContent(option) {
                                SelectionContainer {
                                    Markdown(option.labelOrValue, mdColors, MdTypography(), Modifier)
                                }
                            }
                        }
                        when (state.allowSuggestion) {
                            true -> options + RadioContent(RadioOption(null, state.suggestion)) {
                                TextField(state.suggestion, viewModel::setSuggestion)
                            }
                            false -> options
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
                            onClick = viewModel::toggleIsOpen,
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

