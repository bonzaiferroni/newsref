package streetlight.app.ui.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import streetlight.app.chopui.Scaffold

@Composable
fun <Data> DataCreator(
    title: String,
    item: Data,
    isComplete: Boolean,
    result: String,
    onComplete: ((item: Data) -> Unit)?,
    createData: () -> Unit,
    navigator: Navigator?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    LaunchedEffect(isComplete) {
        if (isComplete) {
            navigator?.pop()
            onComplete?.invoke(item)
        }
    }

    Scaffold(title, navigator) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                content()
                Button(onClick = createData) {
                    Text("Create")
                }
                Text(result)
            }
        }
    }
}