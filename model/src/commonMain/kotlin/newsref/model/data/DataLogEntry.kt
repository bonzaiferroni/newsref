package newsref.model.data

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer

data class LogVariable(
    val id: Int,
    val name: String,
)

sealed class DataLogEntry() {
    abstract val id: Long
    abstract val variableId: Int
    abstract val time: Instant
}

data class IntegerEntry(
    override val id: Long,
    override val variableId: Int,
    override val time: Instant,
    val value: Int,
) : DataLogEntry()

data class JsonEntry(
    override val id: Long,
    override val variableId: Int,
    override val time: Instant,
    val json: String,
) : DataLogEntry()

data class LogPoint<T>(
    val value: T,
    val time: Instant,
)

//data class StringLog(
//    override val id: Long,
//    override val time: Long,
//    val value: String,
//) : DataLog()
//
//data class JsonLog(
//    override val id: Long,
//    override val time: Long,
//    val value: JsonObject
//) : DataLog()
//
//data class FloatLog(
//    override val id: Long,
//    override val time: Long,
//    val value: Float,
//) : DataLog()

sealed class DataLog<T>(
    name: String,
    set: LogSet,
) {
    val name = "${set.name}.$name"
}

class IntegerLog(name: String, set: LogSet) : DataLog<Int>(name, set) { }

class JsonLog<T>(name: String, set: LogSet) : DataLog<T>(name, set)

open class LogSet(val name: String)