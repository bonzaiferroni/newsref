package newsref.db.tables

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import newsref.db.utils.*
import newsref.model.core.*
import newsref.model.data.*
import newsref.model.dto.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

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