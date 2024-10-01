package newsref.krawly.utils

import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import kotlinx.datetime.Clock
import newsref.db.models.NewsArticle
import newsref.db.utils.toUrlOrNull
import newsref.model.data.*
import newsref.model.dto.DocumentInfo
import newsref.model.dto.LinkInfo
import newsref.model.utils.tryParseUrl

fun Doc.read(lead: Lead, outlet: Outlet, newsArticle: NewsArticle?): DocumentInfo {
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
                val url = href.tryParseUrl(outlet.urlParams, lead.url) ?: continue
                links.add(LinkInfo(url = url, anchorText = text, context = element.text))
            }
        }
    }

    return DocumentInfo(
        article = Article(
            headline = this.readHeadline() ?: newsArticle?.headline ?: h1Title ?: this.titleText,
            alternativeHeadline = newsArticle?.alternativeHeadline,
            description = newsArticle?.description ?: this.readDescription(),
            imageUrl = (newsArticle?.image?.firstOrNull()?.url ?: this.readImageUrl())?.toUrlOrNull(),
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
        authors = (newsArticle?.readAuthor() ?: this.readAuthor())?.let { setOf(it) }
    )
}



fun Doc.readMetaContent(vararg propertyValues: String) = propertyValues.firstNotNullOfOrNull {
    var value = this.findFirstOrNull("meta[property=\"$it\"]")?.attributes?.get("content")
    if (value == null)
        value = this.findFirstOrNull("meta[name=\"$it\"]")?.attributes?.get("content")
    value // return
}




