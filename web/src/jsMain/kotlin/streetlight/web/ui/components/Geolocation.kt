package streetlight.web.ui.components

import web.geolocation.Geolocation
import web.geolocation.GeolocationCoordinates
import web.navigator.navigator

fun Geolocation.getCurrentPosition(
    successCallback: (position: GeolocationCoordinates) -> Unit,
) {
    navigator.geolocation.getCurrentPosition({
        successCallback(it.coords)
    }, {
        console.log("Geolocation Error: $it")
    })
}
