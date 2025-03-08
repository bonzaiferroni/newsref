package newsref.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    keyStore: KeyStore = viewModel { KeyStore(dataStore)},
    block: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalKeyStore provides keyStore) {
        block()
    }
}

class KeyStore(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    fun readString(key: String) = dataStore.data.map { it[stringPreferencesKey(key)] ?: "" }
    fun writeString(key: String, value: String) {
        viewModelScope.launch {
            dataStore.edit { it[stringPreferencesKey(key)] = value }
        }
    }
}