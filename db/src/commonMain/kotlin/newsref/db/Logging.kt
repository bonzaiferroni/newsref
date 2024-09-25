package newsref.db

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal val dbLog = Log(LoggerFactory.getLogger("db"))

class Log(
    private val logger: Logger
) {
    fun logInfo(message: String) {
        logger.info(message)
    }

    fun logError(message: String) {
        logger.error(message)
    }

    fun logWarn(message: String) {
        logger.warn(message)
    }

    fun logDebug(message: String) {
        logger.debug(message)
    }

    fun logTrace(message: String) {
        logger.trace(message)
    }
}