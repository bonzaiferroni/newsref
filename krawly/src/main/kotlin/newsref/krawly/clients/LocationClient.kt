package newsref.krawly.clients

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import newsref.krawly.globalKtor
import kabinet.model.GeoPoint

class LocationClient(
    private val key: String,
) {
    suspend fun fetchPlaceGeometry(name: String): Geometry? {
        val url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json" +
                "?fields=geometry" +
                "&input=${name.toEscapedValue()}" +
                "&inputtype=textquery" +
                "&key=$key"
        val response = globalKtor.get(url)
        if (response.status == HttpStatusCode.OK) {
            return response.body<PlacesSearchResponse>().candidates.firstOrNull()?.geometry
        } else {
            return null
        }
    }
}

fun String.toEscapedValue(): String =
    java.net.URLEncoder.encode(this, "UTF-8")


@Serializable
data class PlacesSearchResponse(
    @SerialName("candidates")
    val candidates: List<Place>,
    @SerialName("status")
    val status: PlacesSearchStatus,
    @SerialName("error_message")
    val errorMessage: String? = null,
    @SerialName("info_messages")
    val infoMessages: List<String>? = null
)

@Serializable
data class Place(
    @SerialName("geometry")
    val geometry: Geometry? = null
)

@Serializable
data class Geometry(
    @SerialName("location")
    val location: LatLngLiteral,
    @SerialName("viewport")
    val viewport: Bounds
)

@Serializable
data class LatLngLiteral(
    @SerialName("lat")
    val lat: Double,
    @SerialName("lng")
    val lng: Double
)

@Serializable
data class Bounds(
    @SerialName("northeast")
    val northeast: LatLngLiteral,
    @SerialName("southwest")
    val southwest: LatLngLiteral
)

@Serializable
enum class PlacesSearchStatus {
    @SerialName("OK")
    Ok,
    @SerialName("ZERO_RESULTS")
    ZeroResults,
    @SerialName("INVALID_REQUEST")
    InvalidRequest,
    @SerialName("OVER_QUERY_LIMIT")
    OverQueryLimit,
    @SerialName("REQUEST_DENIED")
    RequestDenied,
    @SerialName("UNKNOWN_ERROR")
    UnknownError
}

fun LatLngLiteral.toGeoPoint() = GeoPoint(lat, lng)