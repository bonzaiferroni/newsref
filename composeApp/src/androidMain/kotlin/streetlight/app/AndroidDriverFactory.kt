package streetlight.app

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import streetlight.app.sqldem.MainActivity

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver =
        AndroidSqliteDriver(
            PantryDb.Schema,
            MainActivity.applicationContext(),
            "DATABASE_NAME"
        )
}