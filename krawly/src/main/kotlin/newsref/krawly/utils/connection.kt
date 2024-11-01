package newsref.krawly.utils

import java.net.InetSocketAddress
import java.net.Socket

fun isNetworkAvailable(): Boolean {
	return try {
		Socket().use { socket ->
			socket.connect(InetSocketAddress("8.8.8.8", 53), 1500) // Google's DNS
			true
		}
	} catch (e: Exception) {
		false
	}
}