package newsref.db.services

import newsref.db.DbService
import newsref.db.globalConsole
import newsref.db.model.User
import newsref.db.tables.UserRow
import newsref.db.tables.UserTable
import newsref.db.tables.fromModel
import newsref.db.utils.RESOURCE_PATH
import newsref.db.utils.createOrUpdate
import newsref.db.utils.jsonDecoder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.io.File

class UserService: DbService() {
	suspend fun initUsers() = dbQuery {
		val userFile = File("$RESOURCE_PATH/config/users.json")
		if (!userFile.exists()) return@dbQuery false
		val users = jsonDecoder.decodeFromString<List<User>>(userFile.readText())
		for (user in users) {
			UserRow.createOrUpdate(UserTable.username eq user.username) {
				globalConsole.logTrace("createUsers", "creating ${user.username}")
				fromModel(user)
			}
		}
		users.isNotEmpty()
	}
}