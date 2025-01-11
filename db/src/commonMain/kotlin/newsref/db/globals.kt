package newsref.db

import io.github.cdimascio.dotenv.dotenv
import newsref.db.log.LogConsole

val globalConsole = LogConsole()

val environment = dotenv() { directory = "../.env" }