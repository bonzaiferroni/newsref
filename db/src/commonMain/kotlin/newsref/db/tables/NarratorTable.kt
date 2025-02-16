package newsref.db.tables

import newsref.db.model.Narrator
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*

object NarratorTable : IntIdTable("narrator") {
    val vectorModelId = reference("vector_model_id", VectorModelTable, ReferenceOption.CASCADE).index()
    val name = text("name")
    val bio = text("bio")
    val chatModelUrl = text("chat_model_url")
}

internal fun ResultRow.toNarrator() = Narrator(
    id = this[NarratorTable.id].value,
    vectorModelId = this[NarratorTable.vectorModelId].value,
    name = this[NarratorTable.name],
    bio = this[NarratorTable.bio],
    chatModelUrl = this[NarratorTable.chatModelUrl],
)