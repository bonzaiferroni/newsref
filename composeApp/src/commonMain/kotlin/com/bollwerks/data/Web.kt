package com.bollwerks.data

import io.ktor.client.HttpClient

val web by lazy { HttpClient() }