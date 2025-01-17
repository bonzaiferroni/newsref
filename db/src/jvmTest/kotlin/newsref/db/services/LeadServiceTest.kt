package newsref.db.services

import newsref.db.DbTest
import newsref.db.tables.LeadResultTable
import newsref.db.tables.LeadTable
import newsref.db.tables.LeadJobTable
import newsref.model.data.FetchResult
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.count
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LeadServiceTest : DbTest() {
	private val resultMap = mapOf(
		FetchResult.RELEVANT to 2,
		FetchResult.IRRELEVANT to 1,
		FetchResult.TIMEOUT to 1
	)

	@BeforeTest
	fun initData() {
//		transaction {
//			val hostRow = HostTable.insert {
//				it[name] = "Axios"
//				it[core] = "axios.com"
//				it[disallowed] = emptyList()
//				it[junkParams] = emptyList()
//				it[domains] = listOf("axios.com")
//			}
//			LeadTable.insert { it[url] = "http://apnews.com/article_headline" }
//			val leadRow = LeadTable.insert {
//				it[hostId] = hostRow[HostTable.id]
//				it[url] = "http://axios.com/article_headline"
//			}
//			resultMap.forEach { (fetchResult, count) ->
//				repeat(count) {
//					LeadResultTable.insert {
//						it[leadId] = leadRow[LeadTable.id]
//						it[result] = fetchResult
//						it[attemptedAt] = (Clock.System.now() - 1.days).toLocalDateTimeUTC()
//					}
//				}
//			}
//			LeadResultTable.insert {
//				it[leadId] = leadRow[LeadTable.id]
//				it[result] = FetchResult.RELEVANT
//				it[attemptedAt] = (Clock.System.now() - 3.days).toLocalDateTimeUTC()
//			}
//		}
	}

	@Test
	fun `getOpenJobs returns leads`() = dbQuery {
		val leads = LeadService().getOpenLeads()
		assertEquals(2, leads.size)
	}
}