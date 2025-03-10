package newsref.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

val LocalKeyStore = staticCompositionLocalOf<KeyStore> {
    error("No Nav provided")
}

@Composable
fun ProvideKeyStore(
    block: @Composable () -> Unit
) {
    val settings = Settings()
    val keyStore = remember { KeyStore(settings) }
    CompositionLocalProvider(LocalKeyStore provides keyStore) {
        block()
    }
}

class KeyStore(
    base: Settings? = null
) {
    init {
        if (base != null) {
            if (_settings != null) error("Keystore base already initialized")
            _settings = base
        }
    }

    companion object {
        private var _settings: Settings? = null
        val settings get() = _settings ?: error("settings not initialized")
    }

    fun readString(key: String) = settings.getStringOrNull(key)
    fun writeString(key: String, value: String) = settings.putString(key, value)

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