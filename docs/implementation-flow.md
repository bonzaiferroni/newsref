```kt
// streetlight.model Request
@Serializable data class Request()

// streetlight.server.data.Request RequestTable
object RequestTable : IntIdTable() { }
class RequestEntity(id: EntityID<Int>) : IntEntity(id) { }

// streetlight.server.data.Request RequestService
class RequestService : DataService<Request, RequestEntity>("Requests", RequestEntity) { }

// streetlight.server.plugins configureApiRoutes()
applyServiceRouting(RequestService())

// streetlight.app.io RequestDao
class RequestDao(private val client: ApiClient, ) { }

// streetlight.app Injection
single { RequestDao(get()) }

// streetlight.app.ui.data RequestEditorScreen
@Composable fun RequestEditorScreen(id: Int?, navigator: Navigator?) { }

// streetlight.app.ui.data RequestListScreen
@Composable fun RequestListScreen(navigator: Navigator?) { }

// streetlight.app Navigation
scene("/Requests") { RequestListScreen(navigator) }
scene("/Request/{id}?") { RequestEditorScreen(it.getId(), navigator) }



```