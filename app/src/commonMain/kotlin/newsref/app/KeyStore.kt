package newsref.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

internal const val dataStoreFileName = "keystore.preferences_pb"

val LocalKeyStore = staticCompositionLocalOf<KeyStore> {
    error("No Nav provided")
}

@Composable
fun ProvideKeyStore(
    dataStore: DataStore<Preferences>,
    block: @Composable () -> Unit
) {
    val keyStore = remember { KeyStore(dataStore) }
    CompositionLocalProvider(LocalKeyStore provides keyStore) {
        block()
    }
}

class KeyStore(
    store: DataStore<Preferences>? = null
) {
    init {
        if (store != null) {
            if (_dataStore != null) error("data store already initialized")
            _dataStore = store
        }
    }

    val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun readString(key: String) = dataStore.data.map { it[stringPreferencesKey(key)] ?: "" }
    fun writeString(key: String, value: String) {
        coroutineScope.launch {
            dataStore.edit { it[stringPreferencesKey(key)] = value }
        }
    }
    inline fun <reified T> readObject(crossinline default: () -> T): Flow<T> {
        val key = stringPreferencesKey(T::class.simpleName ?: error("Must use a type with a name"))
        return dataStore.data.map {
            val json = it[key]
            if (json != null) {
                Json.decodeFromString(json)
            } else {
                default()
            }
        }
    }

    inline fun <reified T> writeObject(value: T) {
        val key = stringPreferencesKey(T::class.simpleName ?: error("Must use a type with a name"))
        coroutineScope.launch {
            dataStore.edit {
                it[key] = Json.encodeToString(value)
            }
        }
    }

    companion object {
        private var _dataStore: DataStore<Preferences>? = null
        val dataStore get() = _dataStore ?: error("data store not initialized")
    }
}