package newsref.db.tables

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.db.utils.toLocalDateTimeUtc
import newsref.db.model.FetchResult
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

class LeadTableTest : DbTest() {

	private val resultMap = mapOf(
		FetchResult.RELEVANT to 2,
		FetchResult.IRRELEVANT to 1,
		FetchResult.TIMEOUT to 1
	)

	@BeforeTest
	fun initData() {
		transaction {
			val hostRow = HostTable.insert {
				it[name] = "Axios"
				it[disallowed] = emptyList()
				it[junkParams] = emptyList()
				it[domains] = listOf("axios.com")
			}
			val leadRow = LeadTable.insert {
				it[hostId] = hostRow[HostTable.id]
				it[url] = "http://axios.com/article_headline"
			}
			resultMap.forEach { (resultType, count) ->
				repeat(count) {
					LeadResultTable.insert {
						it[leadId] = leadRow[LeadTable.id]
						it[result] = resultType
						it[attemptedAt] = (Clock.System.now() - 1.days).toLocalDateTimeUtc()
					}
				}
			}
			LeadResultTable.insert {
				it[leadId] = leadRow[LeadTable.id]
				it[result] = FetchResult.RELEVANT
				it[attemptedAt] = (Clock.System.now() - 3.days).toLocalDateTimeUtc()
			}
		}
	}

	@Test
	fun `getHostResults returns mapped Results`() = transaction {
		// val map = LeadRow.getHostResults(1, 100)
		// assertEquals(resultMap, map)
	}
}