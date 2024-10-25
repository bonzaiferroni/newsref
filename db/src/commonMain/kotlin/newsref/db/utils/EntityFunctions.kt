package newsref.db.utils

import newsref.model.core.Url
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.createOrUpdate(
	find: Op<Boolean>,
	modify: Ent.() -> Unit
) = createOrUpdate({ find }, modify)

fun <Id : Comparable<Id>, Ent : Entity<Id>> EntityClass<Id, Ent>.createOrUpdate(
	find: SqlExpressionBuilder.() -> Op<Boolean>,
	modify: Ent.() -> Unit
): Ent {
	val row = this.find(find).firstOrNull()
	return if (row == null) {
		this.new { modify() }
	} else {
		modify(row)
		row
	}
}

internal fun Column<String>.sameUrl(url: Url): Op<Boolean> {
	val list = mutableListOf(url.href.lowercase())
	if (url.domain.startsWith("www."))
		list.add(url.href.replaceFirst("www.", "").lowercase())
	else {
		val variation = "${url.scheme}://www.${url.domain}${url.fullPath}".lowercase()
		list.add(variation)
	}
	return this.lowerCase() inList list
}
fun Column<String>.sameAs(other: String) = this.lowerCase() eq other.lowercase()

operator fun <T> SizedIterable<T>.plus(item: T): SizedCollection<T> {
	return SizedCollection(this.toList() + item)
}