package newsref.db.core

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.append

internal class VectorColumnOp(
	private val left: Column<Embedding>,
	private val right: Column<Embedding>,
	private val op: String
) : Op<Float>() {
	override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
		append(left, " $op ", right)
	}
}