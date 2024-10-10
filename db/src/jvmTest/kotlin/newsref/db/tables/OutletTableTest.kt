package newsref.db.tables

import newsref.db.DbTest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test

class OutletTableTest : DbTest() {
	@BeforeTest
	fun initData() {
		transaction {
			OutletTable.insert {
				it[name] = "Axios"
				it[disallowed] = emptyList()
				it[urlParams] = emptyList()
				it[domains] = listOf("www.axios.com")
			}
			OutletTable.insert {
				it[name] = "The Atlantic"
				it[disallowed] = emptyList()
				it[urlParams] = emptyList()
				it[domains] = listOf("theatlantic.com")
			}
		}
	}

	@Test
	fun `findByHost should return host row without www`() = transaction {
		val outlet = OutletRow.findByHost("axios.com")
		checkNotNull(outlet)
		assert(outlet.name == "Axios")
	}

	@Test
	fun `findByHost should return host with www`() = transaction {
		val outlet = OutletRow.findByHost("www.theatlantic.com")
		checkNotNull(outlet)
		assert(outlet.name == "The Atlantic")
	}
}