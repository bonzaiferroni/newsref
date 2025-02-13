package newsref.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class DbTest(
	var useRealDb: Boolean = false,
) {
	@BeforeTest
	fun setup() {
		val env = readEnvFromDirectory("../.env")
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