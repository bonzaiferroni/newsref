package newsref.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class DbService(val maxAttempts: Int = 1) {
    suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            maxAttempts = this@DbService.maxAttempts
            block()
        }
}
