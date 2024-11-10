package newsref.db.core

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.append

internal class VectorValueOp(
	private val left: Column<Embedding>,
	private val right: Embedding,
	private val op: String
) : Op<Float>() {
	override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
		append(left, " $op ", "'${left.columnType.notNullValueToDB(right)}'")
	}
}