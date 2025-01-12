package newsref.dashboard

enum class AppScreen(
    val title: String,
    val route: String,
) {
    Start(title = "Start", route = "start"),
    Hello(title = "Hello", route = "hello")
}