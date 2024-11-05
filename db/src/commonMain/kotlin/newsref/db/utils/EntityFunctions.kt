package newsref.db.utils

import newsref.db.tables.FeedSourceRow
import newsref.db.tables.FeedSourceTable
import newsref.model.core.Url
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.createOrUpdate(
	find: Op<Boolean>,
	modify: Ent.(Boolean) -> Unit,
) = createOrUpdate({ find }, modify)

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.createOrUpdate(
	find: SqlExpressionBuilder.() -> Op<Boolean>,
	modify: Ent.(Boolean) -> Unit,
	): Ent {
	val row = this.find(find).firstOrNull()
	return if (row == null) {
		this.new { modify(false) }
	} else {
		row.modify(true)
		row
	}
}

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.updateFirst(
	find: Op<Boolean>,
	modify: Ent.() -> Unit,
) = updateFirst({ find }, modify)

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.updateFirst(
	find: SqlExpressionBuilder.() -> Op<Boolean>,
	modify: Ent.() -> Unit,
) = this.find(find).toList().firstOrNull()?.let(modify)

internal fun Column<String>.sameUrl(url: Url): Op<Boolean> {
	val (urlA, urlB) = if (url.domain.startsWith("www."))
		Pair(url.href.lowercase(), url.href.replaceFirst("www.", "").lowercase())
	else {
		Pair(url.href.lowercase(), "${url.scheme}://www.${url.domain}${url.fullPath}".lowercase())
	}
	return this.lowerCase().eq(urlA) or this.lowerCase().eq(urlB)
}
fun Column<String>.sameAs(other: String) = this.lowerCase() eq other.lowercase()

operator fun <T> SizedIterable<T>.plus(item: T): SizedCollection<T> {
	return SizedCollection(this.toList() + item)
}