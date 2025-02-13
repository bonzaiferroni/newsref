package newsref.app

actual fun getPlatform(): Platform = object : Platform { override val name = "Desktop"}