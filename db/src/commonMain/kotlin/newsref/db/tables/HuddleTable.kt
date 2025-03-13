package newsref.db.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object HuddleTable : LongIdTable("huddle") {
    val askerId = reference("asker_id", UserTable, ReferenceOption.CASCADE)
    val question = text("question")
}

object HuddleCommentTable : Table("huddle_comment") {
    val huddleId = reference("huddle_id", HuddleTable, ReferenceOption.CASCADE)
    val commentId = reference("comment_id", CommentTable, ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(huddleId, commentId)
}

object CommentTable : LongIdTable("comment") {
    val userId = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val text = text("text")
}