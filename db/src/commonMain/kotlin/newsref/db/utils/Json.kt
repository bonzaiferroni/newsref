package newsref.db.utils

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

val prettyPrintJson = Json { prettyPrint = true; ignoreUnknownKeys = true }
val jsonDecoder = Json { ignoreUnknownKeys = true}