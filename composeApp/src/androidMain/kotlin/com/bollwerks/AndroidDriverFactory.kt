package com.bollwerks

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bollwerks.PantryDb
import com.bollwerks.sqldem.MainActivity

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        AndroidSqliteDriver(
            PantryDb.Schema,
            MainActivity.applicationContext(),
            "DATABASE_NAME"
        )
}