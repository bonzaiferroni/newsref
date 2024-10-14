package newsref.db.tables

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.data.ResultType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class LeadTableTest : DbTest() {

	private val resultMap = mapOf(
		ResultType.RELEVANT to 2,
		ResultType.IRRELEVANT to 1,
		ResultType.TIMEOUT to 1
	)

	@BeforeTest
	fun initData() {
		transaction {
			val outletRow = HostTable.insert {
				it[name] = "Axios"
				it[disallowed] = emptyList()
				it[junkParams] = emptyList()
				it[domains] = listOf("axios.com")
			}
			val leadRow = LeadTable.insert {
				it[hostId] = outletRow[HostTable.id]
				it[url] = "http://axios.com/article_headline"
			}
			resultMap.forEach { (resultType, count) ->
				repeat(count) {
					LeadResultTable.insert {
						it[leadId] = leadRow[LeadTable.id]
						it[result] = resultType
						it[attemptedAt] = (Clock.System.now() - 1.days).toLocalDateTimeUTC()
					}
				}
			}
			LeadResultTable.insert {
				it[leadId] = leadRow[LeadTable.id]
				it[result] = ResultType.RELEVANT
				it[attemptedAt] = (Clock.System.now() - 3.days).toLocalDateTimeUTC()
			}
		}
	}

	@Test
	fun `getOutletResults returns mapped Results`() = transaction {
		val map = LeadRow.getOutletResults(1, 1.days)
		assertEquals(resultMap, map)
	}
}