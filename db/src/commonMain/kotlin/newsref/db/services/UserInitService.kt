package newsref.db.services

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import klutch.db.DbService
import klutch.db.model.User
import klutch.db.tables.UserTable
import newsref.db.utils.RESOURCE_PATH
import newsref.db.utils.jsonDecoder
import klutch.utils.toLocalDateTimeUtc
import org.jetbrains.exposed.sql.insertIgnore
import java.io.File

class UserInitService: DbService() {
	suspend fun initUsers() = dbQuery {
		val userFile = File("$RESOURCE_PATH/config/users.json")
		if (!userFile.exists()) return@dbQuery false
		val users = jsonDecoder.decodeFromString<List<User>>(userFile.readText())
		for (user in users) {
			UserTable.insertIgnore {
				it[this.name] = user.name
				it[this.username] = user.username
				it[this.hashedPassword] = user.hashedPassword
				it[this.salt] = user.salt
				it[this.email] = user.email
				it[this.roles] = user.roles.map { it.name }
				it[this.avatarUrl] = user.avatarUrl
				it[this.createdAt] = user.createdAt.toLocalDateTime(TimeZone.UTC)
				it[this.updatedAt] = Clock.System.now().toLocalDateTimeUtc()
			}
		}
		users.isNotEmpty()
	}
}