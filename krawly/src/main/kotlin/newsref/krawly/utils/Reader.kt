package newsref.krawly.utils

import com.eygraber.uri.Url
import it.skrape.selects.Doc
import kotlinx.datetime.Clock
import newsref.db.models.NewsArticle
import newsref.model.data.*
import newsref.model.dto.DocumentInfo
import newsref.model.dto.LinkInfo
import newsref.model.utils.tryParseTrustedUrl

fun Doc.read(sourceUrl: Url, outlet: Outlet, newsArticle: NewsArticle?): DocumentInfo {
    val contents = mutableSetOf<String>()
    val links = mutableListOf<LinkInfo>()
    // var newsArticle = this
    var h1Title: String? = null
    var wordCount = 0
    for (element in allElements) {
        if (element.isLinkContent()) continue
        if (element.isHeading()) {
            if (h1Title == null && element.tagName == "h1") {
                h1Title = element.text
            }
        }
        if (element.isContent()) {
            contents.add(element.text)
            wordCount += element.text.wordCount()
            for ((text, href) in element.eachLink) {
                val url = href.tryParseTrustedUrl(outlet.urlParams, sourceUrl) ?: continue
                links.add(LinkInfo(url = url, anchorText = text, context = element.text))
            }
        }
    }
    val urlString = newsArticle?.url ?: this.readUrl()
    val docUrl = urlString?.tryParseTrustedUrl(outlet.urlParams, sourceUrl)
    val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: this.readImageUrl()
    val imageUrl = imageUrlString?.tryParseTrustedUrl(outlet.urlParams, sourceUrl)

    return DocumentInfo(
        docUrl = docUrl,
        outletId = outlet.id,
        article = Article(
            headline = this.readHeadline() ?: newsArticle?.headline ?: h1Title ?: this.titleText,
            alternativeHeadline = newsArticle?.alternativeHeadline,
            description = newsArticle?.description ?: this.readDescription(),
            imageUrl = imageUrl,
            section = newsArticle?.articleSection,
            keywords = newsArticle?.keywords,
            wordCount = newsArticle?.wordCount ?: wordCount,
            isFree = newsArticle?.isAccessibleForFree,
            thumbnail = newsArticle?.thumbnailUrl,
            language = newsArticle?.inLanguage,
            commentCount = newsArticle?.commentCount,
            accessedAt = Clock.System.now(),
            publishedAt = newsArticle?.readPublishedAt() ?: this.readPublishedAt(),
            modifiedAt = newsArticle?.readModifiedAt() ?: this.readModifiedAt()
        ),
        contents = contents,
        links = links,
        authors = (newsArticle?.readAuthor() ?: this.readAuthor())?.let { setOf(it) },
        type = newsArticle?.let { SourceType.ARTICLE }
            ?: this.readType() ?: SourceType.UNKNOWN,
    )
}
