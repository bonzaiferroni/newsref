package newsref.db.utils

import newsref.model.core.Url
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.lowerCase

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