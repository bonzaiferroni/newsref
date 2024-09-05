package streetlight.web.content

import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.check.radio
import io.kvision.form.check.radioGroup
import io.kvision.form.form
import io.kvision.form.text.text
import streetlight.web.*
import io.kvision.html.*
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import streetlight.model.Request
import streetlight.model.dto.EventInfo
import streetlight.web.components.card
import streetlight.web.io.EventStore
import streetlight.web.io.RequestStore
import streetlight.web.io.SongStore

val eventStore = EventStore()
val requestStore = RequestStore()
val songStore = SongStore()

fun Div.eventProfile(id: Int) {

    val panel = vPanel(spacing = Constants.defaultGap) {
        hPanel(spacing = Constants.defaultGap) {
            link("back", "#/event/${id - 1}")
            link("next", "#/event/${id + 1}")
        }
    }
    launchedEffect {
        try {
            val info = eventStore.getInfo(id)
            // add elements to the page
            panel.addElements(id, info)
        } catch (e: Exception) {
            p("(nope: $id)")
            console.log(e)
        }
    }
}

suspend fun VPanel.addElements(id: Int, info: EventInfo) {
    image("img/bridge.jpg") {
        width = 100.perc
    }
    h1(info.user.name + " @ " + info.location.name)
    p(info.event.description ?: description)
    card {
        p {
            span("now playing: ")
            span(info.currentRequest?.songName)
        }
        p {
            span("next up: ")
            span(info.requests.joinToString(", ") { it.songName })
        }
    }
    h2("Request a song")
    form {
        hPanel(spacing = Constants.defaultGap, justify = JustifyContent.SPACEEVENLY) {
            vPanel(spacing = Constants.defaultGap) {
                width = 50.perc
                radioGroup(label = "Options") {
                    radio(true, label = "Luke sings")
                    radio(false, label = "Duet")
                    radio(false, label = "I'll sing solo")
                }
            }
            vPanel(spacing = Constants.defaultGap) {
                width = 50.perc
                text() {
                    placeholder = "Your name (optional)"
                }
                text() {
                    placeholder = "Other notes (optional)"
                }
            }
        }
    }
    val songs = songStore.getAll()
    songs.forEach { song ->
        val requested = info.requests.any { it.songId == song.id }
        card {
            hPanel(justify = JustifyContent.SPACEBETWEEN) {
                vPanel {
                    p(song.name)
                    p(song.artist)
                }
                button(if (requested) "requested" else "request") {
                    disabled = requested
                    onClickLaunch {
                        val request = Request(eventId = info.event.id, songId = song.id)
                        val result = requestStore.create(request)
                        console.log(result)
                    }
                }
            }
        }
    }
}