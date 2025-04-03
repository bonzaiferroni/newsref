package newsref.db.core

import kotlinx.datetime.Clock
import newsref.db.model.Log
import kotlin.reflect.KClass

class LogBook {
    val logs = mutableListOf<Log>()

    fun write(source: KClass<*>, subject: String, message: String) {
        logs.add(
            Log(
                origin = source.simpleName ?: error("cannot be anonymous"),
                subject = subject,
                message = message,
                time = Clock.System.now()
            )
        )
    }

    fun finalize(pageId: Long? = null) = logs.map { it.copy(pageId = pageId) }
}