package newsref.krawly

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import newsref.db.connectDb
import newsref.db.dbTables
import newsref.db.readEnvFromDirectory
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class DbTest(
    var useRealDb: Boolean = false,
) {

    val env = readEnvFromDirectory("../.env")

    @BeforeTest
    fun setup() {
        if (useRealDb) {
            connectDb(env)
        } else {
            TestDatabase.connect()
            TestDatabase.initDatabase(*dbTables.toTypedArray())
        }
    }

    @AfterTest
    fun teardown() {
        if (!useRealDb) TestDatabase.cleanupDatabase(*dbTables.toTypedArray())
    }

    protected fun <T> dbQuery(block: suspend Transaction.() -> T): T =
        runBlocking { newSuspendedTransaction(Dispatchers.IO) { block() } }
}