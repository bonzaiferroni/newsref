package newsref.db.services

import newsref.model.core.HuddleType

typealias HuddleAdapterMap = Map<HuddleType, HuddleAdapter>

val globalHuddleAdapters = mapOf(
    HuddleType.EditArticleType to ArticleTypeAdapter
)