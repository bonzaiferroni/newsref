package newsref.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import io.pondlib.compose.ui.controls.bg
import io.pondlib.compose.ui.theme.Pond
import io.pondlib.compose.ui.theme.ProvideBookColors
import io.pondlib.compose.utils.modifyIfNotNull

@Composable
fun Card(
    shape: Shape = Pond.ruler.rounded,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ProvideBookColors {
        Column(
            modifier = modifier
                .bg(Pond.localColors.surface, shape)
                .modifyIfNotNull(onClick) { this.clickable(onClick = it) }
                .padding(Pond.ruler.innerPadding)
        ) {
            content()
        }
    }
}