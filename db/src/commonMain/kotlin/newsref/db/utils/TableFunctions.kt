package newsref.db.utils

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnSet
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

internal fun <T : Table> T.read(
    columns: List<Column<*>> = this.columns,
    block: SqlExpressionBuilder.(T) -> Op<Boolean>
) = this.select(columns)
    .where { block(this@read) }

internal fun <T : Table> T.readFirstOrNull(
    columns: List<Column<*>> = this.columns,
    block: SqlExpressionBuilder.(T) -> Op<Boolean>
) = this.select(columns)
    .where { block(this@readFirstOrNull) }
    .firstOrNull()

internal fun <T : Table> T.readFirst(
    columns: List<Column<*>> = this.columns,
    block: SqlExpressionBuilder.(T) -> Op<Boolean>
) = this.select(columns)
    .where { block(this@readFirst) }
    .first()

internal fun <Id : Comparable<Id>, T : IdTable<Id>> T.readById(
    id: Id,
    columns: List<Column<*>> = this.columns,
) = this.select(columns)
    .where { this@readById.id.eq(id) }
    .first()

internal fun <Id : Comparable<Id>, T : IdTable<Id>> T.readByIdOrNull(
    id: Id,
    columns: List<Column<*>> = this.columns,
) = this.select(columns)
    .where { this@readByIdOrNull.id.eq(id) }
    .firstOrNull()

internal fun <Id : Comparable<Id>, T : IdTable<Id>> T.readId(
    block: SqlExpressionBuilder.(T) -> Op<Boolean>
) = this.select(this.id)
    .where { block(this@readId) }
    .first()[this.id].value

internal fun <Id : Comparable<Id>, T : IdTable<Id>> T.readIdOrNull(
    block: SqlExpressionBuilder.(T) -> Op<Boolean>
) = this.select(this.id)
    .where { block(this@readIdOrNull) }
    .firstOrNull()?.let { it[this.id].value }

internal fun <Id : Comparable<Id>, T : IdTable<Id>> T.updateById(
    id: Id,
    body: T.(UpdateStatement) -> Unit
) = this.update(where = { this@updateById.id.eq(id) }, body = body)