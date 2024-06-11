```kt
// streetlight.model Performance
@Serializable data class Performance()

// streetlight.server.data.performance PerformanceTable
object PerformanceTable : IntIdTable() { }
class PerformanceEntity(id: EntityID<Int>) : IntEntity(id) { }

// streetlight.server.data.performance PerformanceService
class PerformanceService : DataService<Performance, PerformanceEntity>("performances", PerformanceEntity) { }

// streetlight.server.plugins configureApiRoutes()
applyServiceRouting(PerformanceService())

// streetlight.app.io PerformanceDao
class PerformanceDao(private val client: ApiClient, ) { }

// streetlight.app Injection
single { PerformanceDao(get()) }

// streetlight.app.ui.data PerformanceEditorScreen
@Composable fun PerformanceEditorScreen(id: Int?, navigator: Navigator?) { }

// streetlight.app.ui.data PerformanceListScreen
@Composable fun PerformanceListScreen(navigator: Navigator?) { }

// streetlight.app Navigation
scene("/performances") { PerformanceListScreen(navigator) }
scene("/performance/{id}?") { PerformanceEditorScreen(it.getId(), navigator) }



```