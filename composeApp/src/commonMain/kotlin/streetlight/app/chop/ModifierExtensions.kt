package streetlight.app.chop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import streetlight.app.chop.Constants.BASE_PADDING

fun Modifier.addBasePadding() = this.padding(BASE_PADDING)
fun Arrangement.addGap() = this.spacedBy(BASE_PADDING)