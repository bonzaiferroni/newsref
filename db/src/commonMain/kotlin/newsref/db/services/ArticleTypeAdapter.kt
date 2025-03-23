package newsref.db.services

import newsref.db.model.Huddle
import newsref.db.model.HuddleOption
import newsref.db.tables.NewsArticleTable
import newsref.model.core.ArticleType
import newsref.model.core.HuddleType
import newsref.model.data.HuddleKey
import org.jetbrains.exposed.sql.update
import kotlin.time.Duration.Companion.minutes

object ArticleTypeAdapter : HuddleAdapter(
    type = HuddleType.EditArticleType,
    duration = 60.minutes
) {
    override suspend fun readOptions(key: HuddleKey): List<HuddleOption> = listOf(
        HuddleOption(DESCRIPTION_REPORT, ArticleType.Report.name),
        HuddleOption(DESCRIPTION_PERSPECTIVE, ArticleType.Perspective.name),
        HuddleOption(DESCRIPTION_ANALYSIS, ArticleType.Analysis.name),
        HuddleOption(DESCRIPTION_INVESTIGATION, ArticleType.Investigation.name)
    )

    override suspend fun updateDatabase(consensus: String, huddle: Huddle) {
        val value = ArticleType.valueOf(consensus)
        NewsArticleTable.update({ NewsArticleTable.pageId.eq(huddle.pageId) }) {
            it[articleType] = value
            it[articleTypeHuddleId] = huddle.id
        }
    }

    override suspend fun readCurrentValue(key: HuddleKey) =
        NewsArticleTable.select(NewsArticleTable.articleType)
            .where { NewsArticleTable.pageId.eq(key.pageId) }
            .first()[NewsArticleTable.articleType].name

    override suspend fun readGuide(key: HuddleKey) = "[Placeholder for EditArticleType Guide]"
}

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