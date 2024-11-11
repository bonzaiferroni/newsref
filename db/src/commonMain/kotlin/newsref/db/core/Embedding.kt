package newsref.db.core

data class Embedding(
	val id: Long = 0,
	val sourceId: Long = 0,
	val vector: FloatArray,
	val model: String,
)