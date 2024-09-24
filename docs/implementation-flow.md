```kt
// newsref.model Request
@Serializable data class Request()

// newsref.server.data.Request RequestTable
object RequestTable : IntIdTable() { }
class RequestEntity(id: EntityID<Int>) : IntEntity(id) { }

// newsref.server.data.Request RequestService
class RequestService : DataService<Request, RequestEntity>("Requests", RequestEntity) { }

// newsref.server.plugins configureApiRoutes()
applyServiceRouting(RequestService())

// newsref.app.io RequestDao
class RequestDao(private val client: ApiClient, ) { }

// newsref.app Injection
single { RequestDao(get()) }

// newsref.app.ui.data RequestEditorScreen
@Composable fun RequestEditorScreen(id: Int?, navigator: Navigator?) { }

// newsref.app.ui.data RequestListScreen
@Composable fun RequestListScreen(navigator: Navigator?) { }

// newsref.app Navigation
scene("/Requests") { RequestListScreen(navigator) }
scene("/Request/{id}?") { RequestEditorScreen(it.getId(), navigator) }



```