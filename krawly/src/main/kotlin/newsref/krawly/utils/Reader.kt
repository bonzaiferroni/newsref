package newsref.krawly.utils

import it.skrape.selects.Doc
import kotlinx.datetime.Clock
import newsref.db.globalConsole
import newsref.db.models.NewsArticle
import newsref.model.core.*
import newsref.model.data.*
import newsref.model.dto.DocumentInfo
import newsref.model.dto.LinkInfo

private val console = globalConsole.getHandle("Reader")

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
                val url = href.toCheckedWithContextOrNull(outlet, sourceUrl) ?: continue
                if (url.isLikelyAd()) continue
                links.add(LinkInfo(url = url, anchorText = text, context = element.text))
            }
        }
    }
    console.logDebug("found ${links.size} links")
    val urlString = newsArticle?.url ?: this.readUrl()
    val docUrl = urlString?.toCheckedWithContextOrNull(outlet, sourceUrl)
    val imageUrlString = newsArticle?.image?.firstOrNull()?.url ?: this.readImageUrl()
    val imageUrl = imageUrlString?.toCheckedWithContextOrNull(outlet, sourceUrl)
    val outletName = newsArticle?.publisher?.name ?: this.readOutletName()

    return DocumentInfo(
        docUrl = docUrl,
        outletId = outlet.id,
        outletName = outletName,
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
