package newsref.dashboard.utils

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.buildAnnotatedString

fun ClipboardManager.setRawText(text: String) = setText(buildAnnotatedString { append(text) })