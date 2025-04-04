package newsref.db.model

import newsref.model.data.TitleEnum

// this is not a comprehensive list but intended to filter out misleading meta info
enum class DocumentType(override val title: String): TitleEnum {
    Unknown("Unknown"),
    NewsArticle("News Article"),
    ResearchArticle("Research Article"),
    PressRelease("Press Release"),
    TechSupport("Tech Support"),
    WebsitePolicy("Website Policy"),
    Missing("Missing Document"),
    Educational("Educational"),
    Profile("Personal Profile or Biography"),
    GeneralInformation("General Information"),
}