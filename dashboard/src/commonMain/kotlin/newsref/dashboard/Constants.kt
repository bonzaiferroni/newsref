package newsref.dashboard

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val baseSpacing = 16.dp
val halfSpacing = 8.dp
val cornerRadius = 12.dp
val basePadding = PaddingValues(baseSpacing)
val halfPadding = PaddingValues(halfSpacing)
val innerPadding = PaddingValues(4.dp)
val emptyEmoji = "\uD83E\uDEB9"
val roundedCorners = RoundedCornerShape(cornerRadius)
val roundedHeader = RoundedCornerShape(
    topStart = cornerRadius, // Round top-left corner
    topEnd = cornerRadius,   // Round top-right corner
    bottomStart = 0.dp, // Square bottom-left corner
    bottomEnd = 0.dp    // Square bottom-right corner
)