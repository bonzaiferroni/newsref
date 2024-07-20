package streetlight.app.ui.debug.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import streetlight.app.ui.core.AppScaffold

@Composable
fun DataEditor(
    title: String,
    isComplete: Boolean,
    isCreate: Boolean,
    result: String,
    createData: () -> Unit,
    navigator: Navigator?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    LaunchedEffect(isComplete) {
        if (isComplete) {
            navigator?.goBack()
        }
    }

    AppScaffold(title, navigator) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                content()
                Button(onClick = createData) {
                    Text(if (isCreate) "Create" else "Update")
                }
                Text(result)
            }
        }
    }
}

