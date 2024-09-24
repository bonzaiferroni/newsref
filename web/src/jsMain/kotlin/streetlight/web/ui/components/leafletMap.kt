package streetlight.web.ui.components

import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.maps.Maps.Companion.L
import io.kvision.maps.externals.leaflet.geo.LatLng
import io.kvision.maps.externals.leaflet.map.LeafletMap
import io.kvision.panel.FlexPanel
import io.kvision.panel.SimplePanel
import io.kvision.state.ObservableValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import streetlight.web.coScope
import web.geolocation.GeolocationCoordinates
import web.navigator.navigator

class StreetMap(
    val origin: LatLng = delMarCircle,
    val debug: Boolean = false,
    className: String? = null
) : SimplePanel(className) {
    private val debugMsg = ObservableValue("")
    private var _map: LeafletMap? = null
    private val container: FlexPanel = col("w-full")
    private val mapDiv: Div = container.div(className = "w-full h-96 rounded") {
        id = "map"
    }

    val map get() = _map

    init {
        coScope.launch {
            var element = mapDiv.getElement()
            while (element == null) {
                delay(1)
                element = mapDiv.getElement()
            }
            _map = L.map(element).setView(origin, 15)
                .setTileLayer(tileLayers[0])
        }
        container.row(justify = JustifyContent.END) {
            if (debug)
                button("my location").onClick {
                    navigator.geolocation.getCurrentPosition {
                        map?.setView(it)
                    }
                }
            menuButton("layers") {
                options = tileLayers.map {
                    MenuButtonOption(it.name) {
                        map?.setTileLayer(it)
                    }
                }
            }
        }.expand()
    }

    suspend fun awaitInflate() {
        while (_map == null) {
            delay(1)
        }
    }
}

fun Container.streetMap(origin: LatLng = delMarCircle, debug: Boolean = false): StreetMap {
    val map = StreetMap(origin, debug)
    this.add(map)
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

fun LeafletMap.removeLayers() {
    this.eachLayer({ this.removeLayer(it) })
}

fun LeafletMap.setTileLayer(option: LayerOption): LeafletMap {
    this.removeLayers()
    this.setZoom(minOf(this.getZoom().toInt(), option.maxZoom))
    this.setMaxZoom(option.maxZoom)
    L.tileLayer(option.url) {
        attribution = "$baseAttribution${option.attribution?.let { " | $it" } ?: ""}"
    }.addTo(this)
    return this
}

val delMarCircle = LatLng(39.726, -104.847)

val baseAttribution = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"

val tileLayers = listOf(
    LayerOption(
        "Humanitarian",
        "https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png",
        "<a href=\"https://www.hotosm.org/\" target=\"_blank\">HOT</a>",
        20
    ),
    LayerOption(
        "Detailed",
        "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
        null,
        19
    ),
    LayerOption(
        "Cycling",
        "https://{s}.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png",
        "<a href=\"https://github.com/cyclosm/cyclosm-cartocss-style\" target=\"_blank\">CyclOSM</a>",
        20
    ),
    LayerOption(
        "Satellite",
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        "&copy; <a href=\"https://www.esri.com/en-us/home\" target=\"_blank\">Esri</a>"
    ),
    LayerOption(
        "Dark",
        "https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png",
        "&copy; <a href=\"https://carto.com/\" target=\"_blank\">Carto</a>"
    ),
    LayerOption(
        "Light",
        "https://cartodb-basemaps-a.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png",
        "&copy; <a href=\"https://carto.com/\" target=\"_blank\">Carto</a>"
    ),
    LayerOption(
        "Services",
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}",
        "&copy; <a href=\"https://www.esri.com/en-us/home\" target=\"_blank\">Esri</a>"
    ),
    LayerOption(
        "Watercolor",
        "https://tiles.stadiamaps.com/tiles/stamen_watercolor/{z}/{x}/{y}.jpg",
        "&copy; <a href=\"https://stadiamaps.com/\" target=\"_blank\">Stadia Maps</a>"
    ),
)

data class LayerOption(val name: String, val url: String, val attribution: String? = null, val maxZoom: Int = 22)