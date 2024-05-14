package streetlight.app.data

import io.ktor.client.HttpClient

val web by lazy { HttpClient() }