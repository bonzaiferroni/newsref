package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.db.tables.*
import newsref.db.tables.LeadResultTable
import newsref.db.tables.LeadTable
import newsref.db.tables.OutletTable
import newsref.db.utils.toLocalDateTimeUTC
import newsref.model.data.ResultType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class LeadServiceTest : DbTest() {
	private val resultMap = mapOf(
		ResultType.RELEVANT to 2,
		ResultType.IRRELEVANT to 1,
		ResultType.TIMEOUT to 1
	)

	@BeforeTest
	fun initData() {
		transaction {
			val outletRow = OutletTable.insert {
				it[name] = "Axios"
				it[disallowed] = emptyList()
				it[urlParams] = emptyList()
				it[domains] = listOf("axios.com")
			}
			LeadTable.insert { it[url] = "http://apnews.com/article_headline" }
			val leadRow = LeadTable.insert {
				it[outletId] = outletRow[OutletTable.id]
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
	fun `getOpenJobs returns leads`() = dbQuery {
		val leads = LeadService().getOpenJobs()
		assertEquals(2, leads.size)
	}
}