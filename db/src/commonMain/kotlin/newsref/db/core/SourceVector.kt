package newsref.db.core

//data class SourceVector(
//	val id: Long = 0,
//	val sourceId: Long = 0,
//	val vector: FloatArray,
//	val model: String,
//)

data class VectorModel(
	val id: Int,
	val name: String,
)

data class SourceDistance(
	val originId: Long,
	val targetId: Long,
	val modelId: Int,
	val distance: Float,
)

data class DistanceInfo(
	val sourceId: Long,
	val modelId: Int,
	val distance: Float,
	val title: String?,
	val url: Url,
)
