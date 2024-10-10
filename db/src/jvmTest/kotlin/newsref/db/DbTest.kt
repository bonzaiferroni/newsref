package newsref.db

import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class DbTest {
	@BeforeTest
	fun setup() {
		TestDatabase.connect()
		TestDatabase.initDatabase(*dbTables.toTypedArray())
	}

	@AfterTest
	fun teardown() {
		TestDatabase.cleanupDatabase(*dbTables.toTypedArray())
	}
}