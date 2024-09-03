package streetlight.serverjs

var host = "http://localhost:8080"

fun init(h: String, eventId:Int) {
    host = h
    refreshEvent(eventId)
}

fun refreshEvent(eventId: Int) {
    console.log("refreshing event")
}