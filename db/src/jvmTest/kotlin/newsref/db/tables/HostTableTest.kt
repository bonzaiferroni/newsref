package newsref.db.tables

import newsref.db.DbTest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest

class HostTableTest : DbTest() {
	@BeforeTest
	fun initData() {
		transaction {
			HostTable.insert {
				it[name] = "Axios"
				it[disallowed] = emptyList()
				it[junkParams] = emptyList()
				it[domains] = listOf("www.axios.com")
			}
			HostTable.insert {
				it[name] = "The Atlantic"
				it[disallowed] = emptyList()
				it[junkParams] = emptyList()
				it[domains] = listOf("theatlantic.com")
			}
		}
	}
}