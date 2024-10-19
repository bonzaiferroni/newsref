package newsref.db.utils

import newsref.model.core.Url
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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

fun Column<String>.sameAs(url: Url) = this.lowerCase() eq url.href.lowercase()
fun Column<String>.sameAs(other: String) = this.lowerCase() eq other.lowercase()

operator fun <T> SizedIterable<T>.plus(item: T): SizedCollection<T> {
	return SizedCollection(this.toList() + item)
}