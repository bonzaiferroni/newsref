package newsref.db.services

import kotlinx.datetime.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import newsref.db.*
import newsref.db.tables.*
import newsref.model.data.*
import newsref.model.utils.toInstantFromEpoch
import org.jetbrains.exposed.sql.*

class DataLogService : DbService() {

    suspend fun readVariable(name: String) = dbQuery {
        LogVariableTable.select(LogVariableTable.id)
            .where { LogVariableTable.name.eq(name) }
            .firstOrNull()?.let { it[LogVariableTable.id].value }
            ?: LogVariableTable.insertAndGetId {
                it[LogVariableTable.name] = name
            }.value
    }

    suspend inline fun <reified T> write(variableId: Int, value: T) = dbQuery {
        when (value) {
            is Int -> writeInt(variableId, value)
        }
    }

    suspend inline fun <reified T> write(log: DataLog<T>, value: T) = dbQuery {
        val variableId = readVariable(log.name)
        when (log) {
            is IntegerLog -> writeInt(variableId, value as Int)
            is JsonLog<T> -> writeJson(variableId, value)
        }
    }

    suspend inline fun <reified T> read(variableId: Int, log: DataLog<T>) = dbQuery {
        when (log) {
            is IntegerLog -> readInt(variableId) as T?
            is JsonLog<T> -> readJson(variableId) as T?
        }
    }

    suspend inline fun <reified T> read(log: DataLog<T>) = dbQuery {
        val variableId = readVariable(log.name)
        read(variableId, log)
    }

    suspend inline fun <reified T> read(log: DataLog<T>, since: Instant) = dbQuery {
        val variableId = readVariable(log.name)
        when (log) {
            is IntegerLog -> readInts(variableId, since)
            is JsonLog<T> -> readJsons(variableId, since)
        } as List<LogPoint<T>>
    }

    suspend fun writeInt(variableId: Int, value: Int) = dbQuery {
        IntegerLogTable.insert {
            it[IntegerLogTable.variableId] = variableId
            it[IntegerLogTable.time] = Clock.System.now().epochSeconds
            it[IntegerLogTable.value] = value
        }
    }

    suspend fun readInt(variableId: Int) = dbQuery {
        IntegerLogTable.select(IntegerLogTable.value)
            .where { IntegerLogTable.variableId.eq(variableId) }
            .orderBy(IntegerLogTable.time, SortOrder.DESC)
            .firstOrNull()?.let { it[IntegerLogTable.value] }
    }

    suspend fun readInts(sinceTime: Instant, sinceId: Long = 0) = dbQuery {
        IntegerLogTable.selectAll()
            .where { IntegerLogTable.id.greater(sinceId) and IntegerLogTable.time.greater(sinceTime.epochSeconds) }
            .orderBy(IntegerLogTable.id, SortOrder.DESC)
            .map { it.toIntegerEntry() }
    }

    suspend fun readInts(variableId: Int, sinceTime: Instant, sinceId: Long = 0) = dbQuery {
        IntegerLogTable.selectAll()
            .where {
                IntegerLogTable.variableId.eq(variableId) and
                        IntegerLogTable.id.greater(sinceId) and
                        IntegerLogTable.time.greater(sinceTime.epochSeconds)
            }
            .orderBy(IntegerLogTable.id, SortOrder.DESC)
            .map { LogPoint(it[IntegerLogTable.value], it[IntegerLogTable.time].toInstantFromEpoch()) }
    }

    suspend inline fun <reified T> readJson(variableId: Int) = dbQuery {
        val json = JsonLogTable.select(JsonLogTable.json)
            .where { JsonLogTable.variableId.eq(variableId) }
            .orderBy(JsonLogTable.time, SortOrder.DESC)
            .firstOrNull()?.let { it[JsonLogTable.json] } ?: return@dbQuery null
        Json.decodeFromString<T>(json)
    }

    suspend inline fun <reified T> readJsons(log: DataLog<T>, since: Instant) = dbQuery {
        val variableId = readVariable(log.name)
        readJsons<T>(variableId, since)
    }

    suspend inline fun <reified T> readJsons(variableId: Int, since: Instant) = dbQuery {
        JsonLogTable.select(JsonLogTable.json, JsonLogTable.time)
            .where { JsonLogTable.variableId.eq(variableId) and JsonLogTable.time.greater(since.epochSeconds) }
            .orderBy(JsonLogTable.time, SortOrder.DESC)
            .map {
                val json = it[JsonLogTable.json]
                val value = Json.decodeFromString<T>(it[JsonLogTable.json])
                LogPoint(value, it[JsonLogTable.time].toInstantFromEpoch())
            }
    }

    suspend inline fun <reified T> writeJson(variableId: Int, value: T) = dbQuery {
        JsonLogTable.insert {
            it[JsonLogTable.variableId] = variableId
            it[JsonLogTable.time] = Clock.System.now().epochSeconds
            it[JsonLogTable.json] = Json.encodeToString(serializer(), value)
        }
    }
}