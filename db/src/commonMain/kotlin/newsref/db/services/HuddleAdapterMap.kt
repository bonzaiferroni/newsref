package newsref.db.services

import newsref.model.data.HuddleType

typealias HuddleAdapterMap = Map<HuddleType, HuddleAdapter>

val globalHuddleAdapters = mapOf(
    HuddleType.EditArticleType to ArticleTypeAdapter,
    HuddleType.EditChapterTitle to ChapterTitleAdapter,
)