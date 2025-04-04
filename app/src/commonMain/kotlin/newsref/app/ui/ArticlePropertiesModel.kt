package newsref.app.ui

import newsref.app.blip.core.StateModel
import newsref.app.io.HuddleStore
import newsref.model.data.Page
import newsref.model.data.ArticleType

class ArticlePropertiesModel(
    val page: Page,
    val huddleStore: HuddleStore = HuddleStore()
): StateModel<ArticlePropertiesState>(ArticlePropertiesState(page)) {

    fun toggleEditingArticleType() {
        setState { it.copy(editingArticleType = !it.editingArticleType) }
    }

    fun selectArticleType(articleType: ArticleType) {
        setState { it.copy(articleType = articleType) }
    }

    fun sendArticleTypeEdit() {
//        if (!stateNow.isValidArticleType) return
//        viewModelScope.launch {
//            val id = huddleStore.createHuddle(HuddleSeed(
//                pageId = article.pageId,
//                type = HuddleType.EditArticleType,
//                value = stateNow.articleType.name,
//                comment = stateNow.comment
//            ))
//            println(id)
//        }
    }

    fun setComment(comment: String) {
        setState { it.copy(comment = comment) }
    }
}

data class ArticlePropertiesState(
    val page: Page,
    val articleType: ArticleType = ArticleType.Unknown,
    val comment: String = "",
    val editingArticleType: Boolean = false
) {
    val isValidArticleType get() = articleType != ArticleType.Unknown
            && articleType != page.articleType
            && comment.isNotBlank()
}