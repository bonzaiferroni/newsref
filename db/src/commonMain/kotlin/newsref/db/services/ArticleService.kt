package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ArticleTable
import newsref.db.tables.NewsArticleTable
import newsref.db.tables.toArticle
import newsref.model.core.ArticleType
import org.jetbrains.exposed.sql.update

class ArticleService() : DbService() {
	suspend fun readBySource(sourceId: Long) = dbQuery {
		ArticleTable.select(ArticleTable.columns)
			.where { ArticleTable.sourceId eq sourceId }
			.firstOrNull()?.toArticle()
	}
}