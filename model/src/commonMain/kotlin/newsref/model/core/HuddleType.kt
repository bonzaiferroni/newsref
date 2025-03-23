package newsref.model.core

import kotlin.time.Duration.Companion.minutes

enum class HuddleType {
    ChapterSourceRelevance,
    CreateChapter,
    EditArticleType,
}

data class HuddleOption<T>(
    val label: String,
    val value: T,
)

val editArticleTypeOptions = listOf(
    HuddleOption(DESCRIPTION_REPORT, ArticleType.Report),
    HuddleOption(DESCRIPTION_PERSPECTIVE, ArticleType.Perspective),
    HuddleOption(DESCRIPTION_ANALYSIS, ArticleType.Analysis),
    HuddleOption(DESCRIPTION_INVESTIGATION, ArticleType.Investigation)
)

val editArticleTypeDuration = 60.minutes

const val DESCRIPTION_REPORT =
    "A **report** conveys just the basic facts about the event or issue: who, what, where, when, why. " +
            "It is also called straight news. It is written objectively and with a neutral perspective."

const val DESCRIPTION_PERSPECTIVE =
    "A **perspective** or opinion article describes an issue from the author's point of view. " +
            "It draws from the experience or position of the author to back up its main points. " +
            "It includes op-eds and editorials and all other articles published as opinions."

const val DESCRIPTION_ANALYSIS =
    "An **analysis** explores an issue in depth, informed by experts and peer-reviewed research. " +
            "It describes and gives a rationale for its methods of inquiry. " +
            "It goes beyond basic reporting by offering more than a neutral perspective based on credible sources. "

const val DESCRIPTION_INVESTIGATION =
    "An **investigation** tells the story of how the author discovers new information, " +
            "through individual research, interviews, and following leads. " +
            "It describes the process of discovery and the results."

val editArticleTypeGuide = "[Placeholder for EditArticleType Guide]"

private suspend fun completeArticleType(huddle: Huddle, stringValue: String) {
    val value = ArticleType.valueOf(stringValue)
    articleService.updateArticleType(huddle.pageId, huddle.id, value)
}