package newsref.db.tables

import newsref.db.model.IntegerEntry
import newsref.db.model.JsonEntry
import newsref.model.utils.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

object LogVariableTable : IntIdTable("variable") {
    val name = text("name")
}

object IntegerLogTable : LongIdTable("integer_log") {
    val variableId = reference("variable_id", LogVariableTable, ReferenceOption.CASCADE)
    val value = integer("value")
    val time = long("time")
}

fun ResultRow.toIntegerEntry() = IntegerEntry(
    id = this[IntegerLogTable.id].value,
    variableId = this[IntegerLogTable.variableId].value,
    value = this[IntegerLogTable.value],
    time = this[IntegerLogTable.time].toInstantFromEpoch()
)

object JsonLogTable : LongIdTable("json_log") {
    val variableId = reference("variable_id", LogVariableTable, ReferenceOption.CASCADE)
    val json = text("json")
    val time = long("time")
}

fun ResultRow.toJsonEntry() = JsonEntry(
    id = this[JsonLogTable.id].value,
    variableId = this[JsonLogTable.variableId].value,
    json = this[JsonLogTable.json],
    time = this[JsonLogTable.time].toInstantFromEpoch()
)