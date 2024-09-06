package streetlight.web.content

import io.kvision.core.JustifyContent
import io.kvision.core.onClickLaunch
import io.kvision.form.check.radio
import io.kvision.form.check.radioGroup
import io.kvision.form.form
import io.kvision.form.text.text
import streetlight.web.*
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.state.bind
import io.kvision.utils.perc
import streetlight.web.components.card
import streetlight.web.components.typography

fun Div.eventProfile(id: Int) {
    val model = EventProfileModel(id)
    typography(spacing = Constants.defaultGap) {
        hPanel(spacing = Constants.defaultGap) {
            link("back", "#/event/${id - 1}")
            link("next", "#/event/${id + 1}")
        }
        h1(className = "text-center").bind(model.eventStream) {
            this.content = it.user.name + " @ " + it.location.name
        }
        image("img/bridge.jpg") {
            width = 100.perc
        }
        p().bind(model.eventStream) {
            this.content = it.event.description ?: description
        }
        card {
            p {
                span("now playing: ")
                span().bind(model.eventStream) {
                    this.content = it.currentRequest?.songName
                }
            }
            p {
                span("next up: ")
                span().bind(model.eventStream) {
                    this.content = it.requests.joinToString(", ") { r -> r.songName }
                }
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

        model.songStream.subscribe { songs ->
            songs.forEach { song ->
                card {
                    hPanel(justify = JustifyContent.SPACEBETWEEN) {
                        vPanel {
                            p(song.name)
                            p(song.artist)
                        }
                        button("request") {
                            onClickLaunch {
                                model.makeRequest(song)
                            }
                        }.bind(model.eventStream) {
                            val requested = it.requests.any { r -> r.songId == song.id }
                            this.text = if (requested) "requested" else "request"
                            this.disabled = requested
                        }
                    }
                }
            }
        }
    }
}

