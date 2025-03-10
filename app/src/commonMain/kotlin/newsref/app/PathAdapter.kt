package newsref.app

import newsref.app.blip.nav.NavRoute

// this is how I might support the location bar in the browser, on hold

open class Pathifier {

    val adapters: MutableMap<String, PathAdapter> = mutableMapOf()

    inline fun <reified T: NavRoute> add(
        noinline toRoute: (String) -> T,
        noinline toString: ((T) -> String)? = null,
    ) {
        val className = T::class.simpleName ?: error("must have class name")
        val toStringBase = toString?.let {
            val thing: (NavRoute) -> String = { toString(it as T) }
            thing
        }
        adapters[className] = PathAdapter(toRoute, toStringBase)
    }

    fun toString(route: NavRoute): String? {
        val className = route::class.simpleName ?: return null
        val adapters = adapters[className]
        if (adapters == null || adapters.toString == null) return className
        return "$className/${adapters.toString(route)}"
    }

    fun toRoute(path: String): NavRoute {
        val parts = path.split("/")
        val className = parts.first()
        val adapter = adapters[className] ?: error("no adapter for $className")
        return adapter.toRoute(path)
    }
}

data class PathAdapter(
    val toRoute: (String) -> NavRoute,
    val toString: ((NavRoute) -> String)? = null,
)

object AppPathAdapter : Pathifier() {
    init {
        add({ StartRoute })
        add({ ChapterFeedRoute() }, { pathify(it.feedSpan) })
    }
}

fun pathify(vararg param: Any) = param.joinToString("/")

data class PathArgs(val split: List<String>) {

}