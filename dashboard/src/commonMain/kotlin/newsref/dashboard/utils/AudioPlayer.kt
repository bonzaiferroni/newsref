package newsref.dashboard.utils

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.util.Locale

@Composable
fun AudioPlayer(
    url: String?,
    isPlaying: Boolean,
    onFinished: () -> Unit
) {
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember {
        val mp = mediaPlayerComponent.mediaPlayer()
        mp.events().addMediaPlayerEventListener(
            object : MediaPlayerEventAdapter() {
                override fun finished(mediaPlayer: MediaPlayer?) {
                    super.finished(mediaPlayer)
                    onFinished()
                }

                override fun playing(mediaPlayer: MediaPlayer?) {
                    super.playing(mediaPlayer)
                }
            }
        )
        mp
    }

    val factory = remember { { mediaPlayerComponent } }

    LaunchedEffect(url) {
        if (url != null) {
            println("Playing $url")
            mediaPlayer.media().play(url)
        } else {
            mediaPlayer.controls().stop()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.controls().play()
        } else {
            mediaPlayer.controls().pause()
        }
    }


    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }
    SwingPanel(
        factory = factory,
        background = Color.Transparent,
        modifier = Modifier,
        update = { }
    )
}

private fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}


private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}