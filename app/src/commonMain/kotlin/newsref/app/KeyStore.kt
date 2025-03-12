package newsref.app

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json

class KeyStore() {

    companion object {
        val settings = Settings()
    }

    fun readStringOrNull(key: String) = settings.getStringOrNull(key)
    fun readBooleanOrNull(key: String) = settings.getBooleanOrNull(key)
    fun writeString(key: String, value: String) = settings.putString(key, value)
    fun writeBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)

    inline fun <reified T> readObjectOrNull(): T? = T::class.simpleName?.let { className ->
        readObjectOrNull(className)
    }

    inline fun <reified T> readObjectOrNull(key: String): T? = settings.getStringOrNull(key)?.let {
        Json.decodeFromString(it)
    }

    inline fun <reified T> writeObject(value: T) {
        val className = T::class.simpleName ?: error("Must use type with a name")
        writeObject(className, value)
    }

    inline fun <reified T> writeObject(key: String, value: T) {
        settings.putString(key, Json.encodeToString(value))
    }
}