package newsref.db.log

fun Boolean.logIfTrue(msg: String) = if (this) msg else ""