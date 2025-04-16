package newsref.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import pondui.ui.theme.DefaultColors
import pondui.ui.theme.Pond

@Composable
fun MdTypography(): DefaultMarkdownTypography {
    if (cachedTypography != null) return cachedTypography!!
    val typography = Pond.typo

    cachedTypography = DefaultMarkdownTypography(
        h1 = typography.h1,
        h2 = typography.h2,
        h3 = typography.h3,
        h4 = typography.h4,
        h5 = typography.h4, // todo
        h6 = typography.h4, // todo
        text = typography.body,
        code = typography.body,
        inlineCode = typography.body,
        quote = typography.body,
        paragraph = typography.body,
        ordered = typography.body,
        bullet = typography.body,
        list = typography.body,
        link = typography.body,
        textLink = TextLinkStyles(
            style = SpanStyle()
        )
    )

    return cachedTypography!!
}

var cachedTypography: DefaultMarkdownTypography? = null

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