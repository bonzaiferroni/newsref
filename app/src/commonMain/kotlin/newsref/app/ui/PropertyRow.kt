package newsref.app.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.pondlib.compose.ui.theme.Pond


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyRow(
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Pond.ruler.rowTight,
        verticalArrangement = Pond.ruler.columnTight,
    ) {
        content()
    }
}