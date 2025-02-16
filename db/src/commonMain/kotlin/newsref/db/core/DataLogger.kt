package newsref.db.core

import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import newsref.db.services.*
import newsref.db.model.DataLog
import newsref.db.model.IntegerLog
import kotlin.time.*
import kotlin.time.Duration.Companion.minutes

class DataLogger(
    val logService: DataLogService = DataLogService()
) {
    val coroutineScope get () = CoroutineScope(Dispatchers.IO)
    private val variableIds = mutableMapOf<String, Int>()
    private val variableNames = mutableMapOf<Int, String>()
    private val intObservers = mutableMapOf<String, MutableList<(List<IntegerValue>) -> Unit>>()

    operator fun get(log: IntegerLog): MutableList<(List<IntegerValue>) -> Unit> =
        intObservers[log.name] ?: mutableListOf<(List<IntegerValue>) -> Unit>().also { intObservers[log.name] = it }

    fun start(rewind: Duration = 10.minutes, recheckMillis: Long = 200) = coroutineScope.launch {
        val start = Clock.System.now() - rewind
        var lastIntegerId = 0L
        while (true) {
            lastIntegerId = observeIntegers(start, lastIntegerId)
            delay(recheckMillis)
        }
    }

    fun observe(log: IntegerLog, block: (List<IntegerValue>) -> Unit) = this[log].add(block)

    fun initVariable(name: String) = runBlocking {
        if (variableIds.contains(name)) throw error("variable already take: $name")
        val id = logService.readVariable(name)
        variableIds[name] = id
        id
    }

    inline fun <reified T> write(variableId: Int, value: T) = coroutineScope.launch {
        logService.write<T>(variableId, value)
    }

    inline fun <reified T> read(variableId: Int, log: DataLog<T>) = runBlocking {
        logService.read<T>(variableId, log)
    }

    private suspend fun observeIntegers(start: Instant, lastIntegerId: Long): Long {
        val integerEntries = logService.readInts(start, lastIntegerId)
        val entries = integerEntries.groupBy { it.variableId }
        for ((variableId, list) in entries) {
            val values = list.sortedByDescending { it.time }.map { IntegerValue(it.value, it.time) }
            val variableName = variableNames.getValue(variableId)
            val observers = intObservers[variableName] ?: continue
            for (observer in observers) {
                observer(values)
            }
        }
        return integerEntries.lastOrNull()?.id ?: 0L
    }
}

//class VariableLogger<T>(
//    val log: DataLog<T>,
//    val logger: DataLogger,
//) {
//    val variableId: Int = logger.initVariable(log.name)
//    var value = logger.read<T>(variableId, log)
//
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
//        return value
//    }
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T?) {
//        if (newValue == value) return
//        value = newValue
//        if (newValue == null) return
//        logger.write(variableId, newValue)
//    }
//}

data class IntegerValue(
    val value: Int,
    val time: Instant,
)