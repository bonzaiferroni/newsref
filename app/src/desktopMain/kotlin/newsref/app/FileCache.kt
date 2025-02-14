package newsref.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.File

@Composable
inline fun <reified T> FileCache(path: String, crossinline provider: () -> T): MutableStateFlow<T> {
    val cache = remember {
        val file = File(path)
        val value = if (file.exists()) {
            Json.decodeFromString(serializer(), file.readText())
        } else {
            provider()
        }
        MutableStateFlow(value)
    }

    val value by cache.collectAsState()
    LaunchedEffect(value) {
        println(value)
        File(path).writeText(Json.encodeToString(serializer(), value))
    }
    return cache
}