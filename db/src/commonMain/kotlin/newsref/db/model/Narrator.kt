package newsref.db.model

data class Narrator(
    val id: Int = 0,
    val vectorModelId: Int = 0,
    val name: String,
    val bio: String,
    val chatModelUrl: String,
)