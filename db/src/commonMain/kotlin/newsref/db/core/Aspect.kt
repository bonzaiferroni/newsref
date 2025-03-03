package newsref.db.core

import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder

@Suppress("UNCHECKED_CAST")
open class Aspect<SubType: Aspect<SubType>>(
    val columnSet: ColumnSet
) {
    private val _expressions = mutableListOf<ExpressionWithColumnType<*>>()
    val columns: List<ExpressionWithColumnType<*>> = _expressions

    fun <T> add(expression: ExpressionWithColumnType<T>): ExpressionWithColumnType<T> {
        _expressions.add(expression)
        return expression
    }

    fun where(predicate: SqlExpressionBuilder.(SubType) -> Op<Boolean>) =
        columnSet.select(columns)
            .where { predicate(this, this@Aspect as SubType) }
}