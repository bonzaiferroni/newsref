package newsref.app.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pondui.ui.theme.Pond


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyRow(
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Pond.ruler.rowUnit,
        verticalArrangement = Pond.ruler.columnUnit,
    ) {
        content()
    }
}