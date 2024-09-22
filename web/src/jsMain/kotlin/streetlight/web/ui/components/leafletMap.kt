package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.maps.Maps.Companion.L
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.map.LeafletMap
import kotlinx.coroutines.delay
import web.geolocation.GeolocationCoordinates
import web.navigator.navigator

suspend fun Container.leafletMap(origin: LatLng = LatLng(39.726, -104.847)): LeafletMap {
    val col = col("w-full")
    val div = col.div(className = "w-full h-96 rounded")
    var element = div.getElement()
    while (element == null) {
        delay(1)
        element = div.getElement()
    }
    val map = L.map(element).setView(origin, 15)
        .setTileLayer(tileLayers[0].url)

    col.row {
        button("my location").onClick {
            navigator.geolocation.getCurrentPosition {
                map.setView(it)
            }
        }
        menuButton("layers") {
            options = tileLayers.map { MenuButtonOption(it.name) {
                map.setTileLayer(it.url)
            } }
        }
    }
    return map
}

fun LeafletMap.setView(coords: GeolocationCoordinates) = setView(LatLng(coords.latitude, coords.longitude))

fun LeafletMap.addMarker(point: LatLng, message: String? = null): LeafletMap {
    val marker = L.marker(point).addTo(this)
    if (message != null) {
        marker.bindPopup(message).openPopup()
    }
    return this
}

fun LeafletMap.setTileLayer(url: String): LeafletMap {
    L.tileLayer(url) {
        attribution = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
    }.addTo(this)
    return this
}

val tileLayers = listOf(
    LayerOption("Humanitarian", "http://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png"),
    LayerOption("Detailed", "https://tile.openstreetmap.org/{z}/{x}/{y}.png"),
    LayerOption("Cycling", "https://{s}.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png"),
    LayerOption("Watercolor", "https://tiles.stadiamaps.com/tiles/stamen_watercolor/{z}/{x}/{y}.jpg"),
    LayerOption("Satellite", "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"),
    LayerOption("Dark", "https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png"),
    LayerOption("Light", "https://cartodb-basemaps-a.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png"),
    LayerOption("?", "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}")
)

data class LayerOption(val name: String, val url: String)