package streetlight.app.services

class BusService {
    val callbacks = mutableMapOf<Class<*>, Any>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified Data> supply(data: Data) {
        val type = Data::class.java
        val callback = callbacks[type] as? ((Data) -> Unit)
        callback?.invoke(data)
        callbacks.remove(type)
    }

    inline fun <reified Data> request(noinline callback: (Data) -> Unit) {
        callbacks[Data::class.java] = callback
    }
}