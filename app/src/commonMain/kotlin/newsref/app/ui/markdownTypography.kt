package newsref.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import newsref.app.blip.theme.DefaultColors
import newsref.app.blip.theme.DefaultTypography

val mdTypography
    @Composable get() = DefaultTypography().let {
        DefaultMarkdownTypography(
            h1 = it.h1,
            h2 = it.h2,
            h3 = it.h3,
            h4 = it.h4,
            h5 = it.h4, // todo
            h6 = it.h4, // todo
            text = it.body,
            code = it.body,
            inlineCode = it.body,
            quote = it.body,
            paragraph = it.body,
            ordered = it.body,
            bullet = it.body,
            list = it.body,
            link = it.body,
            textLink = TextLinkStyles(
                style = SpanStyle()
            )
        )
    }

val mdColors = DefaultColors.let {
    DefaultMarkdownColors(
        text = it.contentBook,
        codeText = it.contentBook,
        inlineCodeText = it.contentBook,
        linkText = it.contentBook,
        codeBackground = it.surfaceBook,
        inlineCodeBackground = it.surfaceBook,
        dividerColor = it.primary,
        tableText = it.contentBook,
        tableBackground = it.surfaceBook
    )
}