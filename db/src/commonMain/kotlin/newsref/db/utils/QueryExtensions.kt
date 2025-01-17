package newsref.db.utils

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder

fun Query.toSqlString(block: (String) -> Unit) = this.also { block(it.prepareSQL(QueryBuilder(false))) }

fun <T> Query.applyIfNotNull(value: T?, block: Query.(T) -> Unit): Query {
    if (value != null) block(value)
    return this
}