package newsref.db.core

import newsref.db.utils.toSqlString
import org.jetbrains.exposed.sql.*

open class Aspect<Self: Aspect<Self, Data>, Data>(
    val columnSet: ColumnSet,
    val toData: ResultRow.() -> Data
) {
    private val _expressions = mutableListOf<ExpressionWithColumnType<*>>()
    val columns: List<ExpressionWithColumnType<*>> = _expressions
    val query get () = columnSet.select(columns)

    fun <T, E: ExpressionWithColumnType<T>> add(expression: E): E {
        _expressions.add(expression)
        return expression
    }

    @Suppress("UNCHECKED_CAST")
    fun where(predicate: SqlExpressionBuilder.(Self) -> Op<Boolean>) =
        query.where { predicate(this, this@Aspect as Self) }

    fun readFirst(predicate: SqlExpressionBuilder.(Self) -> Op<Boolean>) = where(predicate)
        .firstOrNull()?.let { toData(it) }

    fun read(predicate: SqlExpressionBuilder.(Self) -> Op<Boolean>) = where(predicate).map { toData(it) }

    fun read(
        sortBy: Expression<*>,
        orderBy: SortOrder,
        limit: Int,
        predicate: SqlExpressionBuilder.(Self) -> Op<Boolean>
    ) = where(predicate)
        .orderBy(sortBy, orderBy)
        .limit(limit)
        .map { toData(it) }

    fun readAll(
        sortBy: Expression<*>,
        orderBy: SortOrder,
        limit: Int,
    ) = columnSet.select(columns)
        .orderBy(sortBy, orderBy)
        .limit(limit)
        .map { toData(it) }

    fun any(predicate: SqlExpressionBuilder.(Self) -> Op<Boolean>) = readFirst(predicate) != null
}