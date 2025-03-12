package newsref.db.utils

import kotlinx.datetime.Clock

fun Clock.Companion.epochSecondsNow() = Clock.System.now().epochSeconds