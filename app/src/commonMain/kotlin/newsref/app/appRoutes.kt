package newsref.app

import kotlinx.serialization.Serializable
import newsref.app.core.AppRoute

@Serializable
object StartRoute : AppRoute {
    override val title: String = "Start"
    val owner = Person("Chambers")
}

@Serializable
data class Person(val name: String)