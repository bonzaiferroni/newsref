package newsref.dashboard.ui.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun ConfirmButton(
    text: String,
    onConfirm: () -> Unit
) {
    var confirmed by remember { mutableStateOf(false) }
    val containerColor by animateColorAsState(
        if (confirmed) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
        label = "color"
    )

    Button(
        onClick = {
            if (confirmed) {
                onConfirm()
            } else {
                confirmed = true
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Text(if (confirmed) "$text." else "$text?")
    }
}