package newsref.db

import org.jetbrains.exposed.sql.Transaction
import newsref.db.tables.UserRow
import newsref.db.tables.UserTable

internal fun Transaction.findUser(username: String) = UserRow.find { UserTable.username eq username }.firstOrNull()

internal fun Transaction.findUserIdOrThrow(username: String) = UserTable
    .select(UserTable.id)
    .where { UserTable.username eq username }
    .map { it[UserTable.id] }
    .firstOrNull() ?: throw IllegalArgumentException("User not found")