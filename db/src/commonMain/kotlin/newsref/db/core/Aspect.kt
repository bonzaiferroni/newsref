package newsref.db.core

import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

open class Aspect(
    val columnSet: ColumnSet
) {
    private val _expressions = mutableListOf<ExpressionWithColumnType<*>>()
    val expressions: List<ExpressionWithColumnType<*>> = _expressions

    fun <T> add(expression: ExpressionWithColumnType<T>): ExpressionWithColumnType<T> {
        _expressions.add(expression)
        return expression
    }

    fun where(predicate: SqlExpressionBuilder.() -> Op<Boolean>) =
        columnSet.select(expressions)
            .where(predicate)
}