package newsref.db.services

import kotlinx.datetime.Clock
import newsref.db.DbTest
import newsref.db.tables.*
import newsref.db.tables.HostRow
import newsref.db.tables.fromData
import newsref.db.utils.toCheckedFromTrusted
import newsref.db.utils.plus
import newsref.model.data.Author
import newsref.model.data.Host
import newsref.model.data.Source
import org.jetbrains.exposed.dao.flushCache
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedSourceServiceTest : DbTest(true) {
	@Test
	fun `explore code`() = dbQuery {

	}

	@Test
	fun `poke code`() = dbQuery {

	}
}