package newsref.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.compose.Markdown
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import newsref.app.blip.controls.Button
import newsref.app.blip.controls.FloatyBox
import newsref.app.blip.controls.H2
import newsref.app.blip.controls.RadioContent
import newsref.app.blip.controls.RadioGroup
import newsref.app.blip.controls.RadioOption
import newsref.app.blip.controls.Text
import newsref.app.blip.controls.TextField
import newsref.app.blip.theme.Blip
import newsref.model.data.HuddleKey

@Composable
fun HuddleCreatorBox(
    huddleName: String,
    showBox: Boolean,
    key: HuddleKey,
    onDismiss: () -> Unit,
    viewModel: HuddleCreatorModel = viewModel { HuddleCreatorModel(key) }
) {
    val state by viewModel.state.collectAsState()

    FloatyBox(showBox, onDismiss) {
        Column(
            modifier = Modifier.clip(Blip.ruler.roundBottom)
        ) {
            H2(huddleName)
            RadioGroup(state.selectedValue, viewModel::selectValue) {
                state.options.map { option ->
                    RadioContent(option) {
                        Markdown(option.label, mdColors, mdTypography, Modifier)
                    }
                }.toImmutableList()
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
}

