package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.LinkTable
import newsref.db.tables.SourceTable
import newsref.db.tables.fromData
import newsref.db.utils.toCheckedFromDb
import newsref.db.utils.toLocalDateTimeUTC
import newsref.db.utils.plus
import newsref.model.data.Author
import newsref.model.data.Host
import newsref.model.data.Source
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class FeedSourceServiceTest : DbTest(true) {
	@Test
	fun `explore code`() = dbQuery {

	}

	@Test
	fun `poke code`() = dbQuery {
		val originalUrl = "https://www.businessinsider.com/tesla".toCheckedFromDb()
		val anotherUrl = "https://axios.com/big-news-story".toCheckedFromDb()
		val hostRow = HostRow.new { fromData(Host(core = originalUrl.domain, domains = setOf(originalUrl.domain))) }
		val anotherHostRow = HostRow.new { fromData(Host(core = anotherUrl.domain, domains = setOf(anotherUrl.domain))) }
		val sourceRow = SourceRow.new { fromData(Source(url = originalUrl, seenAt = Clock.System.now()), hostRow) }
		val anotherSourceRow = SourceRow.new { fromData(Source(url = anotherUrl, seenAt = Clock.System.now()), anotherHostRow) }
		val authorRow = AuthorRow.new { fromData(Author(name = "Luke", bylines = setOf("Luke")), hostRow, sourceRow) }
		flushCache()
		authorRow.hosts += anotherHostRow
		authorRow.sources += anotherSourceRow
		flushCache()
		assertEquals(2, authorRow.hosts.count())
		assertEquals(2, authorRow.sources.count())
	}
}