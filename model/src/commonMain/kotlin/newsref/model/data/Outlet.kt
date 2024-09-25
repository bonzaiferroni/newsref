package newsref.model.data

data class Outlet(
    val id: Int,
    val name: String,
    val apex: String,
    val domains: Set<String>,
)