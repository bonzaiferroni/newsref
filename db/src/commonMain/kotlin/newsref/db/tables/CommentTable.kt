package newsref.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CommentTable : LongIdTable("comment") {
    val userId = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val text = text("text")
    val time = datetime("time")
}