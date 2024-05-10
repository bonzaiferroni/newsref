package com.bollwerks

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory constructor() {
    fun create(): SqlDriver
}