package newsref.db.services

import newsref.db.model.Huddle
import newsref.db.tables.PageTable
import newsref.db.utils.readById
import newsref.db.utils.updateById
import newsref.model.data.ArticleType
import newsref.model.data.HuddleType
import newsref.model.data.HuddleKey
import newsref.model.data.HuddleOption

object ArticleTypeAdapter : HuddleAdapter(HuddleType.EditArticleType) {

    fun getKey(pageId: Long) = HuddleKey(
        type = HuddleType.EditArticleType,
        pageId = pageId
    )

    override suspend fun readOptions(key: HuddleKey): List<HuddleOption> = listOf(
        HuddleOption(DESCRIPTION_REPORT, ArticleType.Report.name),
        HuddleOption(DESCRIPTION_PERSPECTIVE, ArticleType.Opinion.name),
        HuddleOption(DESCRIPTION_ANALYSIS, ArticleType.Analysis.name),
        HuddleOption(DESCRIPTION_INVESTIGATION, ArticleType.Investigation.name)
    )

    override suspend fun updateDatabase(consensus: String, huddle: Huddle) {
        val value = ArticleType.valueOf(consensus)
        PageTable.updateById(huddle.pageId) {
            it[articleType] = value
            it[articleTypeHuddleId] = huddle.id
        }
    }

    override suspend fun readCurrentValue(key: HuddleKey) =
        key.pageId?.let {
            PageTable.readById(it, listOf(PageTable.articleType))[PageTable.articleType].name
        } ?: error("Key must have a pageId")

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