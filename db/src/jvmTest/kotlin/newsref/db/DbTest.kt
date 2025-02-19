package newsref.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.junit.After
import org.junit.Before

open class DbTest(
	var useRealDb: Boolean = false,
) {
	@Before
	fun setup() {
		val env = readEnvFromDirectory("../.env")
		if (useRealDb) {
			connectDb(env)
		} else {
			TestDatabase.connect()
			TestDatabase.initDatabase(*dbTables.toTypedArray())
		}
	}

	@After
	fun teardown() {
		if (!useRealDb) TestDatabase.cleanupDatabase(*dbTables.toTypedArray())
	}

	protected fun <T> dbQuery(block: suspend Transaction.() -> T): T =
        runBlocking { newSuspendedTransaction(Dispatchers.IO) { block() } }
}