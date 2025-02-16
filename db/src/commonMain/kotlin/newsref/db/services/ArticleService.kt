package newsref.db.services

import newsref.db.DbService
import newsref.db.tables.ArticleTable
import newsref.db.tables.toArticle

class ArticleService() : DbService() {
	suspend fun readBySource(sourceId: Long) = dbQuery {
		ArticleTable.select(ArticleTable.columns)
			.where { ArticleTable.sourceId eq sourceId }
			.firstOrNull()?.toArticle()
	}
}