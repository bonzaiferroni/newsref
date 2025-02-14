package newsref.app.blip.controls

import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconButton(imageVector: ImageVector, action: () -> Unit) {
    Icon(
        imageVector = imageVector,
        modifier = Modifier.clickable(onClick = action)
    )
}